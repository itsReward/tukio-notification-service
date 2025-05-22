package com.tukio.notificationservice.kafka

import com.tukio.notificationservice.dto.BatchNotificationRequestDTO
import com.tukio.notificationservice.dto.NotificationRequestDTO
import com.tukio.notificationservice.service.NotificationService
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

/**
 * Kafka consumer for notification events
 */
@Component
class NotificationConsumer(
    private val notificationService: NotificationService
) {

    private val logger = LoggerFactory.getLogger(NotificationConsumer::class.java)

    /**
     * Process individual notification requests
     */
    @KafkaListener(topics = ["notifications"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeNotification(request: NotificationRequestDTO) {
        logger.info("Received notification event for user ${request.userId}")

        try {
            notificationService.createNotification(request)
            logger.info("Successfully processed notification for user ${request.userId}")
        } catch (e: Exception) {
            logger.error("Failed to process notification: ${e.message}", e)
        }
    }

    /**
     * Process batch notification requests
     */
    @KafkaListener(topics = ["batch-notifications"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeBatchNotification(request: BatchNotificationRequestDTO) {
        logger.info("Received batch notification event for ${request.userIds.size} users")

        try {
            val sentCount = notificationService.createBatchNotification(request)
            logger.info("Successfully processed batch notification. Sent count: $sentCount")
        } catch (e: Exception) {
            logger.error("Failed to process batch notification: ${e.message}", e)
        }
    }

    /**
     * Process event reminder requests
     */
    @KafkaListener(topics = ["event-reminders"], groupId = "\${spring.kafka.consumer.group-id}")
    fun consumeEventReminder(eventId: Long) {
        logger.info("Received event reminder request for event $eventId")

        try {
            // In a real implementation, we would fetch the event details and user registrations
            // and create notification requests

            // For simplicity, we'll just call the sendEventReminders method
            val sentCount = notificationService.sendEventReminders()
            logger.info("Successfully processed event reminder. Sent count: $sentCount")
        } catch (e: Exception) {
            logger.error("Failed to process event reminder: ${e.message}", e)
        }
    }
}