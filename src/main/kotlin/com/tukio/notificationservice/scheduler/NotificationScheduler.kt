package com.tukio.notificationservice.scheduler

import com.tukio.notificationservice.model.NotificationChannel
import com.tukio.notificationservice.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * Scheduler for notification-related tasks
 */
@Component
class NotificationScheduler(
    private val notificationService: NotificationService
) {

    private val logger = LoggerFactory.getLogger(NotificationScheduler::class.java)

    /**
     * Send reminders for events scheduled for tomorrow
     * Runs daily at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * *")
    fun sendEventReminders() {
        logger.info("Starting scheduled task: Send event reminders")

        try {
            val sentCount = notificationService.sendEventReminders()
            logger.info("Event reminders sent: $sentCount")
        } catch (e: Exception) {
            logger.error("Failed to send event reminders: ${e.message}", e)
        }
    }

    /**
     * Process pending email notifications
     * Runs every 15 minutes
     */
    @Scheduled(fixedRate = 900000) // 15 minutes in milliseconds
    fun processEmailNotifications() {
        logger.info("Starting scheduled task: Process pending email notifications")

        try {
            val processedCount = notificationService.processPendingNotifications(NotificationChannel.EMAIL)
            logger.info("Pending email notifications processed: $processedCount")
        } catch (e: Exception) {
            logger.error("Failed to process pending email notifications: ${e.message}", e)
        }
    }

    /**
     * Process pending push notifications
     * Runs every 5 minutes
     */
    @Scheduled(fixedRate = 300000) // 5 minutes in milliseconds
    fun processPushNotifications() {
        logger.info("Starting scheduled task: Process pending push notifications")

        try {
            val processedCount = notificationService.processPendingNotifications(NotificationChannel.PUSH)
            logger.info("Pending push notifications processed: $processedCount")
        } catch (e: Exception) {
            logger.error("Failed to process pending push notifications: ${e.message}", e)
        }
    }

    /**
     * Process pending in-app notifications
     * Runs every 1 minute
     */
    @Scheduled(fixedRate = 60000) // 1 minute in milliseconds
    fun processInAppNotifications() {
        logger.info("Starting scheduled task: Process pending in-app notifications")

        try {
            val processedCount = notificationService.processPendingNotifications(NotificationChannel.IN_APP)
            logger.info("Pending in-app notifications processed: $processedCount")
        } catch (e: Exception) {
            logger.error("Failed to process pending in-app notifications: ${e.message}", e)
        }
    }
}