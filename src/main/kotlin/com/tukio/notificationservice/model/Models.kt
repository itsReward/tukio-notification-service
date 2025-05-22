package com.tukio.notificationservice.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "notification_templates")
data class NotificationTemplate(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val templateKey: String,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var content: String,

    @Column(nullable = true)
    var description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val channel: NotificationChannel,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val templateType: TemplateType,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

@Entity
@Table(name = "notifications")
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val notificationType: NotificationType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val channel: NotificationChannel,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: NotificationStatus = NotificationStatus.PENDING,

    @Column(nullable = true)
    val referenceId: String? = null,

    @Column(nullable = true)
    val referenceType: String? = null,

    @Column(nullable = true)
    var sentAt: LocalDateTime? = null,

    @Column(nullable = true)
    var deliveredAt: LocalDateTime? = null,

    @Column(nullable = true)
    var readAt: LocalDateTime? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class NotificationChannel {
    EMAIL, PUSH, IN_APP
}

enum class TemplateType {
    HTML, TEXT, PUSH
}

enum class NotificationType {
    EVENT_REGISTRATION,
    EVENT_REMINDER,
    EVENT_CANCELLATION,
    EVENT_UPDATE,
    VENUE_CHANGE,
    SYSTEM_ANNOUNCEMENT
}

enum class NotificationStatus {
    PENDING, SENT, FAILED, DELIVERED, READ
}


