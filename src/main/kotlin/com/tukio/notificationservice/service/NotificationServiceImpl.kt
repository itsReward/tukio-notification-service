package com.tukio.notificationservice.service

import com.tukio.notificationservice.client.EventServiceClient
import com.tukio.notificationservice.client.UserServiceClient
import com.tukio.notificationservice.dto.*
import com.tukio.notificationservice.exception.NotFoundException
import com.tukio.notificationservice.exception.ValidationException
import com.tukio.notificationservice.model.*
import com.tukio.notificationservice.repository.NotificationRepository
import com.tukio.notificationservice.repository.NotificationTemplateRepository
import com.tukio.notificationservice.repository.UserNotificationPreferenceRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class NotificationServiceImpl(
    private val notificationRepository: NotificationRepository,
    private val templateRepository: NotificationTemplateRepository,
    private val preferenceRepository: UserNotificationPreferenceRepository,
    private val userServiceClient: UserServiceClient,
    private val eventServiceClient: EventServiceClient,
    private val emailSender: EmailSenderService,
    private val pushSender: PushNotificationService,
    private val inAppSender: InAppNotificationService
) : NotificationService {

    private val logger = LoggerFactory.getLogger(NotificationServiceImpl::class.java)

    @Value("\${notification.event-reminder.days-before}")
    private val reminderDaysBefore: Int = 1

    @Value("\${notification.batch-size}")
    private val batchSize: Int = 50

    @Transactional
    override fun createNotification(request: NotificationRequestDTO): List<NotificationResponseDTO> {
        // Validate request
        if (request.channels.isEmpty()) {
            throw ValidationException("At least one notification channel must be specified")
        }

        // Get user's notification preferences
        val preferences = preferenceRepository.findByUserIdAndNotificationType(
            request.userId, request.notificationType
        ).orElse(null)

        // Process template
        val (title, content) = processTemplate(request.templateKey, request.templateData)

        // Create notifications for each enabled channel
        val notifications = mutableListOf<Notification>()

        for (channel in request.channels) {
            // Skip channel if user has disabled it
            if (preferences != null && !isChannelEnabled(preferences, channel)) {
                logger.info("User ${request.userId} has disabled ${channel} notifications for ${request.notificationType}")
                continue
            }

            // Create notification entity
            val notification = Notification(
                userId = request.userId,
                title = title,
                content = content,
                notificationType = request.notificationType,
                channel = channel,
                status = NotificationStatus.PENDING,
                referenceId = request.referenceId,
                referenceType = request.referenceType
            )

            notifications.add(notificationRepository.save(notification))

            // Try to send immediately
            when (channel) {
                NotificationChannel.EMAIL -> emailSender.send(notification)
                NotificationChannel.PUSH -> pushSender.send(notification)
                NotificationChannel.IN_APP -> inAppSender.send(notification)
            }
        }

        return notifications.map { it.toResponseDTO() }
    }

    @Transactional
    override fun createBatchNotification(request: BatchNotificationRequestDTO): Int {
        var totalSent = 0

        // Process users in batches to avoid memory issues
        request.userIds.chunked(batchSize).forEach { userIdBatch ->
            // Create individual notifications for each user
            for (userId in userIdBatch) {
                val notificationRequest = NotificationRequestDTO(
                    userId = userId,
                    templateKey = request.templateKey,
                    templateData = request.templateData,
                    channels = request.channels,
                    notificationType = request.notificationType
                )

                try {
                    val notifications = createNotification(notificationRequest)
                    totalSent += notifications.size
                } catch (e: Exception) {
                    logger.error("Failed to create notification for user $userId: ${e.message}")
                }
            }
        }

        return totalSent
    }

    override fun getUserNotifications(
        userId: Long,
        status: NotificationStatus?,
        channel: NotificationChannel?,
        pageable: Pageable
    ): Page<NotificationResponseDTO> {
        val notifications = when {
            status != null && channel != null ->
                notificationRepository.findByUserIdAndStatusAndChannel(userId, status, channel, pageable)
            status != null ->
                notificationRepository.findByUserIdAndStatus(userId, status, pageable)
            channel != null ->
                notificationRepository.findByUserIdAndChannel(userId, channel, pageable)
            else ->
                notificationRepository.findByUserId(userId, pageable)
        }

        return notifications.map { it.toResponseDTO() }
    }

    @Transactional
    override fun markAsRead(notificationId: Long, userId: Long): NotificationResponseDTO {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { NotFoundException("Notification not found with id: $notificationId") }

        // Verify that the notification belongs to the specified user
        if (notification.userId != userId) {
            throw ValidationException("Notification does not belong to user $userId")
        }

        // Only mark as read if it's an in-app notification
        if (notification.channel == NotificationChannel.IN_APP && notification.readAt == null) {
            notification.readAt = LocalDateTime.now()
            notification.updatedAt = LocalDateTime.now()

            return notificationRepository.save(notification).toResponseDTO()
        }

        return notification.toResponseDTO()
    }

    override fun getUnreadCount(userId: Long): Long {
        return notificationRepository.countUnreadInAppNotifications(userId)
    }

    @Transactional
    override fun deleteNotification(notificationId: Long, userId: Long) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { NotFoundException("Notification not found with id: $notificationId") }

        // Verify that the notification belongs to the specified user
        if (notification.userId != userId) {
            throw ValidationException("Notification does not belong to user $userId")
        }

        notificationRepository.delete(notification)
    }

    override fun processTemplate(templateKey: String, templateData: Map<String, String>): Pair<String, String> {
        val template = templateRepository.findByTemplateKey(templateKey)
            .orElseThrow { NotFoundException("Template not found with key: $templateKey") }

        var title = template.title
        var content = template.content

        // Replace variables in title and content
        for ((key, value) in templateData) {
            val placeholder = "{{$key}}"
            title = title.replace(placeholder, value)
            content = content.replace(placeholder, value)
        }

        return Pair(title, content)
    }

    @Transactional
    override fun sendEventReminders(): Int {
        var sentCount = 0

        try {
            // Get events that are happening tomorrow
            val tomorrow = LocalDate.now().plusDays(reminderDaysBefore.toLong())
            val upcomingEvents = eventServiceClient.getUpcomingEvents()

            // Filter events that start tomorrow
            val formatterDate = DateTimeFormatter.ISO_DATE_TIME
            val tomorrowEvents = upcomingEvents.filter {
                val eventDate = LocalDateTime.parse(it.startTime, formatterDate).toLocalDate()
                eventDate == tomorrow
            }

            logger.info("Sending reminders for ${tomorrowEvents.size} events scheduled for tomorrow")

            // For each event, fetch its registrations and send reminders
            for (event in tomorrowEvents) {
                // In a real implementation, we would fetch registrations from event service
                // For this example, we'll simulate it
                val userIds = listOf(1L, 2L, 3L) // This would come from event service

                val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
                val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

                val eventDate = LocalDateTime.parse(event.startTime, formatterDate)
                val eventTime = eventDate.format(timeFormatter)
                val formattedDate = eventDate.format(dateFormatter)

                // Prepare template data
                val templateData = mapOf(
                    "eventName" to event.title,
                    "eventDate" to formattedDate,
                    "eventTime" to eventTime,
                    "eventLocation" to (event.venueName ?: event.location)
                )

                // Create batch notification
                val batchRequest = BatchNotificationRequestDTO(
                    userIds = userIds,
                    templateKey = "EVENT_REMINDER_EMAIL",
                    templateData = templateData,
                    channels = listOf(NotificationChannel.EMAIL, NotificationChannel.IN_APP, NotificationChannel.PUSH),
                    notificationType = NotificationType.EVENT_REMINDER
                )

                sentCount += createBatchNotification(batchRequest)
            }
        } catch (e: Exception) {
            logger.error("Error sending event reminders: ${e.message}", e)
        }

        return sentCount
    }

    @Transactional
    override fun processPendingNotifications(channel: NotificationChannel): Int {
        var processedCount = 0

        try {
            val pageable = Pageable.ofSize(batchSize)
            val pendingNotifications = notificationRepository.findByStatusAndChannelOrderByCreatedAt(
                NotificationStatus.PENDING, channel, pageable
            )

            logger.info("Processing ${pendingNotifications.size} pending notifications for channel $channel")

            for (notification in pendingNotifications) {
                try {
                    when (channel) {
                        NotificationChannel.EMAIL -> emailSender.send(notification)
                        NotificationChannel.PUSH -> pushSender.send(notification)
                        NotificationChannel.IN_APP -> inAppSender.send(notification)
                    }
                    processedCount++
                } catch (e: Exception) {
                    logger.error("Failed to process notification ${notification.id}: ${e.message}")
                }
            }
        } catch (e: Exception) {
            logger.error("Error processing pending notifications: ${e.message}", e)
        }

        return processedCount
    }

    // Helper methods

    private fun isChannelEnabled(preference: UserNotificationPreference, channel: NotificationChannel): Boolean {
        return when (channel) {
            NotificationChannel.EMAIL -> preference.emailEnabled
            NotificationChannel.PUSH -> preference.pushEnabled
            NotificationChannel.IN_APP -> preference.inAppEnabled
        }
    }

    // Extension function to convert Notification to NotificationResponseDTO
    private fun Notification.toResponseDTO(): NotificationResponseDTO {
        return NotificationResponseDTO(
            id = this.id,
            userId = this.userId,
            title = this.title,
            content = this.content,
            notificationType = this.notificationType,
            channel = this.channel,
            status = this.status,
            referenceId = this.referenceId,
            referenceType = this.referenceType,
            sentAt = this.sentAt,
            deliveredAt = this.deliveredAt,
            readAt = this.readAt,
            createdAt = this.createdAt
        )
    }
}