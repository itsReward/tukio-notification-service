package com.tukio.notificationservice.service

import com.tukio.notificationservice.client.UserServiceClient
import com.tukio.notificationservice.exception.NotificationSendException
import com.tukio.notificationservice.model.DeliveryStatus
import com.tukio.notificationservice.model.Notification
import com.tukio.notificationservice.model.NotificationChannel
import com.tukio.notificationservice.model.NotificationDeliveryAttempt
import com.tukio.notificationservice.model.NotificationStatus
import com.tukio.notificationservice.repository.NotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.TemplateEngine
import java.time.LocalDateTime

/**
 * Interface for notification channel senders
 */
interface NotificationSender {
    fun send(notification: Notification): Boolean
}

/**
 * Service for sending email notifications
 */
@Service
class EmailSenderService(
    private val mailSender: JavaMailSender,
    private val userServiceClient: UserServiceClient,
    private val notificationRepository: NotificationRepository,
    private val templateEngine: TemplateEngine,
    @Value("\${notification.email.from}") private val fromEmail: String,
    @Value("\${notification.retry.max-attempts}") private val maxAttempts: Int
) : NotificationSender {

    private val logger = LoggerFactory.getLogger(EmailSenderService::class.java)

    @Transactional
    override fun send(notification: Notification): Boolean {
        if (notification.channel != NotificationChannel.EMAIL) {
            throw IllegalArgumentException("Not an email notification")
        }

        // Check if max attempts reached
        if (notification.deliveryAttempts.size >= maxAttempts) {
            if (notification.status != NotificationStatus.FAILED) {
                notification.status = NotificationStatus.FAILED
                notification.updatedAt = LocalDateTime.now()
                notificationRepository.save(notification)
            }
            return false
        }

        try {
            // Get user info to get email address
            val user = userServiceClient.getUserById(notification.userId)

            // Create the email message
            val message = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(message, true)

            helper.setFrom(fromEmail)
            helper.setTo(user.email)
            helper.setSubject(notification.title)
            helper.setText(notification.content, true) // true means it's HTML content

            // Send the email
            mailSender.send(message)

            // Record successful delivery attempt
            notification.deliveryAttempts.add(
                NotificationDeliveryAttempt(
                    notificationId = notification.id,
                    attemptNumber = notification.deliveryAttempts.size + 1,
                    status = DeliveryStatus.SUCCESS
                )
            )

            // Update notification status
            notification.status = NotificationStatus.SENT
            notification.sentAt = LocalDateTime.now()
            notification.updatedAt = LocalDateTime.now()

            notificationRepository.save(notification)

            logger.info("Email notification ${notification.id} sent to user ${notification.userId}")
            return true
        } catch (e: Exception) {
            logger.error("Failed to send email notification ${notification.id}: ${e.message}")

            // Record failed delivery attempt
            notification.deliveryAttempts.add(
                NotificationDeliveryAttempt(
                    notificationId = notification.id,
                    attemptNumber = notification.deliveryAttempts.size + 1,
                    status = DeliveryStatus.FAILED,
                    errorMessage = e.message
                )
            )

            // Only update status to FAILED if we've reached max attempts
            if (notification.deliveryAttempts.size >= maxAttempts) {
                notification.status = NotificationStatus.FAILED
            }

            notification.updatedAt = LocalDateTime.now()
            notificationRepository.save(notification)

            throw NotificationSendException("Failed to send email: ${e.message}", NotificationChannel.EMAIL.name)
        }
    }
}

/**
 * Service for sending push notifications
 */
@Service
class PushNotificationService(
    private val userServiceClient: UserServiceClient,
    private val notificationRepository: NotificationRepository,
    @Value("\${notification.retry.max-attempts}") private val maxAttempts: Int
) : NotificationSender {

    private val logger = LoggerFactory.getLogger(PushNotificationService::class.java)

    @Transactional
    override fun send(notification: Notification): Boolean {
        if (notification.channel != NotificationChannel.PUSH) {
            throw IllegalArgumentException("Not a push notification")
        }

        // Check if max attempts reached
        if (notification.deliveryAttempts.size >= maxAttempts) {
            if (notification.status != NotificationStatus.FAILED) {
                notification.status = NotificationStatus.FAILED
                notification.updatedAt = LocalDateTime.now()
                notificationRepository.save(notification)
            }
            return false
        }

        try {
            // Get user info to get push token
            val user = userServiceClient.getUserById(notification.userId)

            // Skip if user has no push token
            if (user.pushToken.isNullOrBlank()) {
                logger.warn("User ${notification.userId} has no push token, skipping push notification")

                // Record attempt but mark as success (we're skipping, not failing)
                notification.deliveryAttempts.add(
                    NotificationDeliveryAttempt(
                        notificationId = notification.id,
                        attemptNumber = notification.deliveryAttempts.size + 1,
                        status = DeliveryStatus.SUCCESS,
                        errorMessage = "User has no push token"
                    )
                )

                notification.status = NotificationStatus.SENT
                notification.sentAt = LocalDateTime.now()
                notification.updatedAt = LocalDateTime.now()

                notificationRepository.save(notification)
                return true
            }

            // In a real application, this would integrate with FCM, APNs, or another push service
            // For this example, we'll just simulate sending
            simulatePushNotificationSend(user.pushToken, notification.title, notification.content)

            // Record successful delivery attempt
            notification.deliveryAttempts.add(
                NotificationDeliveryAttempt(
                    notificationId = notification.id,
                    attemptNumber = notification.deliveryAttempts.size + 1,
                    status = DeliveryStatus.SUCCESS
                )
            )

            // Update notification status
            notification.status = NotificationStatus.SENT
            notification.sentAt = LocalDateTime.now()
            notification.updatedAt = LocalDateTime.now()

            notificationRepository.save(notification)

            logger.info("Push notification ${notification.id} sent to user ${notification.userId}")
            return true
        } catch (e: Exception) {
            logger.error("Failed to send push notification ${notification.id}: ${e.message}")

            // Record failed delivery attempt
            notification.deliveryAttempts.add(
                NotificationDeliveryAttempt(
                    notificationId = notification.id,
                    attemptNumber = notification.deliveryAttempts.size + 1,
                    status = DeliveryStatus.FAILED,
                    errorMessage = e.message
                )
            )

            // Only update status to FAILED if we've reached max attempts
            if (notification.deliveryAttempts.size >= maxAttempts) {
                notification.status = NotificationStatus.FAILED
            }

            notification.updatedAt = LocalDateTime.now()
            notificationRepository.save(notification)

            throw NotificationSendException("Failed to send push notification: ${e.message}", NotificationChannel.PUSH.name)
        }
    }

    // Simulated push notification sending
    private fun simulatePushNotificationSend(token: String, title: String, message: String) {
        // In a real application, this would call FCM, APNs, or other push service APIs
        logger.debug("Simulating push notification: Token=$token, Title=$title, Message=$message")
        // Simulate a small delay
        Thread.sleep(100)
    }
}

/**
 * Service for sending in-app notifications
 */
@Service
class InAppNotificationService(
    private val notificationRepository: NotificationRepository,
    @Value("\${notification.retry.max-attempts}") private val maxAttempts: Int
) : NotificationSender {

    private val logger = LoggerFactory.getLogger(InAppNotificationService::class.java)

    @Transactional
    override fun send(notification: Notification): Boolean {
        if (notification.channel != NotificationChannel.IN_APP) {
            throw IllegalArgumentException("Not an in-app notification")
        }

        // Check if max attempts reached
        if (notification.deliveryAttempts.size >= maxAttempts) {
            if (notification.status != NotificationStatus.FAILED) {
                notification.status = NotificationStatus.FAILED
                notification.updatedAt = LocalDateTime.now()
                notificationRepository.save(notification)
            }
            return false
        }

        try {
            // For in-app notifications, we just need to save it to the database
            // and mark it as delivered so it can be retrieved by the client

            // Record successful delivery attempt
            notification.deliveryAttempts.add(
                NotificationDeliveryAttempt(
                    notificationId = notification.id,
                    attemptNumber = notification.deliveryAttempts.size + 1,
                    status = DeliveryStatus.SUCCESS
                )
            )

            // Update notification status
            notification.status = NotificationStatus.DELIVERED // In-app notifications are immediately delivered
            notification.sentAt = LocalDateTime.now()
            notification.deliveredAt = LocalDateTime.now()
            notification.updatedAt = LocalDateTime.now()

            notificationRepository.save(notification)

            logger.info("In-app notification ${notification.id} delivered to user ${notification.userId}")
            return true
        } catch (e: Exception) {
            logger.error("Failed to deliver in-app notification ${notification.id}: ${e.message}")

            // Record failed delivery attempt
            notification.deliveryAttempts.add(
                NotificationDeliveryAttempt(
                    notificationId = notification.id,
                    attemptNumber = notification.deliveryAttempts.size + 1,
                    status = DeliveryStatus.FAILED,
                    errorMessage = e.message
                )
            )

            // Only update status to FAILED if we've reached max attempts
            if (notification.deliveryAttempts.size >= maxAttempts) {
                notification.status = NotificationStatus.FAILED
            }

            notification.updatedAt = LocalDateTime.now()
            notificationRepository.save(notification)

            throw NotificationSendException("Failed to deliver in-app notification: ${e.message}", NotificationChannel.IN_APP.name)
        }
    }
}