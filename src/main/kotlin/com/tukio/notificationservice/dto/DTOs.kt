package com.tukio.notificationservice.dto

import com.tukio.notificationservice.model.NotificationChannel
import com.tukio.notificationservice.model.NotificationStatus
import com.tukio.notificationservice.model.NotificationType
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class NotificationRequestDTO(
    val userId: Long,
    val templateKey: String,
    val templateData: Map<String, String>,
    val channels: List<NotificationChannel> = listOf(NotificationChannel.EMAIL, NotificationChannel.IN_APP),
    val notificationType: NotificationType,
    val referenceId: String? = null,
    val referenceType: String? = null
)

data class NotificationResponseDTO(
    val id: Long,
    val userId: Long,
    val title: String,
    val content: String,
    val notificationType: NotificationType,
    val channel: NotificationChannel,
    val status: NotificationStatus,
    val referenceId: String?,
    val referenceType: String?,
    val sentAt: LocalDateTime?,
    val deliveredAt: LocalDateTime?,
    val readAt: LocalDateTime?,
    val createdAt: LocalDateTime
)

data class BatchNotificationRequestDTO(
    val userIds: List<Long>,
    val templateKey: String,
    val templateData: Map<String, String>,
    val channels: List<NotificationChannel> = listOf(NotificationChannel.EMAIL, NotificationChannel.IN_APP),
    val notificationType: NotificationType
)

/**
 * DTO for notification preferences
 */
data class NotificationPreferenceDTO(
    @field:NotNull(message = "User ID is required")
    val userId: Long,

    @field:NotNull(message = "Notification type is required")
    val notificationType: NotificationType,

    val emailEnabled: Boolean = true,
    val pushEnabled: Boolean = true,
    val inAppEnabled: Boolean = true
)

/**
 * DTO for updating notification preferences
 */
data class UpdatePreferenceRequest(
    val emailEnabled: Boolean? = null,
    val pushEnabled: Boolean? = null,
    val inAppEnabled: Boolean? = null
) {
    /**
     * Validates that at least one field is provided for update
     */
    fun hasUpdates(): Boolean {
        return emailEnabled != null || pushEnabled != null || inAppEnabled != null
    }

    /**
     * Returns a map of non-null fields for logging purposes
     */
    fun getUpdatedFields(): Map<String, Boolean> {
        val updates = mutableMapOf<String, Boolean>()

        emailEnabled?.let { updates["emailEnabled"] = it }
        pushEnabled?.let { updates["pushEnabled"] = it }
        inAppEnabled?.let { updates["inAppEnabled"] = it }

        return updates
    }
}