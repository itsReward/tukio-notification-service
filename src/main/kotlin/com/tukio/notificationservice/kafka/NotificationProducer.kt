package com.tukio.notificationservice.kafka

import com.tukio.notificationservice.dto.BatchNotificationRequestDTO
import com.tukio.notificationservice.dto.NotificationRequestDTO
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

/**
 * Kafka producer for sending notification events
 */
@Component
class NotificationProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    private val logger = LoggerFactory.getLogger(NotificationProducer::class.java)

    /**
     * Send a notification request to Kafka
     */
    fun sendNotification(request: NotificationRequestDTO) {
        logger.info("Sending notification event for user ${request.userId}")

        try {
            val key = "user-${request.userId}"
            kafkaTemplate.send("notifications", key, request)
            logger.info("Notification event sent for user ${request.userId}")
        } catch (e: Exception) {
            logger.error("Failed to send notification event: ${e.message}", e)
            // In a real application, you might implement a retry mechanism or fallback
        }
    }

    /**
     * Send a batch notification request to Kafka
     */
    fun sendBatchNotification(request: BatchNotificationRequestDTO) {
        logger.info("Sending batch notification event for ${request.userIds.size} users")

        try {
            val key = "batch-notification-${System.currentTimeMillis()}"
            kafkaTemplate.send("batch-notifications", key, request)
            logger.info("Batch notification event sent")
        } catch (e: Exception) {
            logger.error("Failed to send batch notification event: ${e.message}", e)
            // In a real application, you might implement a retry mechanism or fallback
        }
    }

    /**
     * Send an event reminder request to Kafka
     */
    fun sendEventReminder(eventId: Long) {
        logger.info("Sending event reminder for event $eventId")

        try {
            val key = "event-reminder-$eventId"
            kafkaTemplate.send("event-reminders", key, eventId)
            logger.info("Event reminder sent for event $eventId")
        } catch (e: Exception) {
            logger.error("Failed to send event reminder: ${e.message}", e)
            // In a real application, you might implement a retry mechanism or fallback
        }
    }
}