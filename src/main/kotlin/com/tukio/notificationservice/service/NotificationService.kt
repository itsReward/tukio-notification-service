package com.tukio.notificationservice.service

import com.tukio.notificationservice.dto.*
import com.tukio.notificationservice.model.NotificationChannel
import com.tukio.notificationservice.model.NotificationStatus
import com.tukio.notificationservice.model.NotificationType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * Service for notification management
 */
interface NotificationService {

    /**
     * Create and send a notification to a user
     *
     * @param request Notification request with user ID, template key, and template data
     * @return The created notification
     */
    fun createNotification(request: NotificationRequestDTO): List<NotificationResponseDTO>

    /**
     * Sends notifications to multiple users using the same template
     *
     * @param request Batch notification request
     * @return Count of notifications sent
     */
    fun createBatchNotification(request: BatchNotificationRequestDTO): Int

    /**
     * Get notifications for a user
     *
     * @param userId User ID
     * @param status Filter by notification status
     * @param channel Filter by notification channel
     * @param pageable Pagination and sorting
     * @return Page of notifications
     */
    fun getUserNotifications(
        userId: Long,
        status: NotificationStatus? = null,
        channel: NotificationChannel? = null,
        pageable: Pageable
    ): Page<NotificationResponseDTO>

    /**
     * Mark a notification as read
     *
     * @param notificationId Notification ID
     * @param userId User ID (for validation)
     * @return The updated notification
     */
    fun markAsRead(notificationId: Long, userId: Long): NotificationResponseDTO

    /**
     * Get count of unread in-app notifications for a user
     *
     * @param userId User ID
     * @return Count of unread notifications
     */
    fun getUnreadCount(userId: Long): Long

    /**
     * Delete a notification
     *
     * @param notificationId Notification ID
     * @param userId User ID (for validation)
     */
    fun deleteNotification(notificationId: Long, userId: Long)

    /**
     * Generate notification content from a template
     *
     * @param templateKey Template key
     * @param templateData Template data for variable substitution
     * @return Processed content
     */
    fun processTemplate(templateKey: String, templateData: Map<String, String>): Pair<String, String>

    /**
     * Send event reminder notifications for upcoming events
     *
     * @return Count of notifications sent
     */
    fun sendEventReminders(): Int

    /**
     * Process pending notifications for a specific channel
     *
     * @param channel Notification channel
     * @return Count of notifications processed
     */
    fun processPendingNotifications(channel: NotificationChannel): Int
}