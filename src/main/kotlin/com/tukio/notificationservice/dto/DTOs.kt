package com.tukio.notificationservice.dto

import com.tukio.notificationservice.model.NotificationChannel
import com.tukio.notificationservice.model.NotificationStatus
import com.tukio.notificationservice.model.NotificationType
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
