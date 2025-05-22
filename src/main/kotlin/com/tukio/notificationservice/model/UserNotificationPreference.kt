package com.tukio.notificationservice.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entity representing user's preferences for receiving notifications
 */
@Entity
@Table(
    name = "user_notification_preferences",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_user_notification_type",
            columnNames = ["user_id", "notification_type"]
        )
    ],
    indexes = [
        Index(name = "idx_user_notification_preferences_user_id", columnList = "user_id"),
        Index(name = "idx_user_notification_preferences_type", columnList = "notification_type")
    ]
)
data class UserNotificationPreference(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 100)
    val notificationType: NotificationType,

    @Column(name = "email_enabled", nullable = false)
    var emailEnabled: Boolean = true,

    @Column(name = "push_enabled", nullable = false)
    var pushEnabled: Boolean = true,

    @Column(name = "in_app_enabled", nullable = false)
    var inAppEnabled: Boolean = true,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Updates the timestamp when the entity is modified
     */
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    /**
     * Checks if the user has enabled any notification channel for this type
     */
    fun hasAnyChannelEnabled(): Boolean {
        return emailEnabled || pushEnabled || inAppEnabled
    }

    /**
     * Checks if a specific channel is enabled
     */
    fun isChannelEnabled(channel: NotificationChannel): Boolean {
        return when (channel) {
            NotificationChannel.EMAIL -> emailEnabled
            NotificationChannel.PUSH -> pushEnabled
            NotificationChannel.IN_APP -> inAppEnabled
        }
    }

    /**
     * Gets a list of enabled channels
     */
    fun getEnabledChannels(): List<NotificationChannel> {
        val enabledChannels = mutableListOf<NotificationChannel>()

        if (emailEnabled) enabledChannels.add(NotificationChannel.EMAIL)
        if (pushEnabled) enabledChannels.add(NotificationChannel.PUSH)
        if (inAppEnabled) enabledChannels.add(NotificationChannel.IN_APP)

        return enabledChannels
    }

    /**
     * Disables all notification channels
     */
    fun disableAllChannels() {
        emailEnabled = false
        pushEnabled = false
        inAppEnabled = false
        updatedAt = LocalDateTime.now()
    }

    /**
     * Enables all notification channels
     */
    fun enableAllChannels() {
        emailEnabled = true
        pushEnabled = true
        inAppEnabled = true
        updatedAt = LocalDateTime.now()
    }

    /**
     * Updates channel preferences
     */
    fun updateChannelPreferences(
        email: Boolean? = null,
        push: Boolean? = null,
        inApp: Boolean? = null
    ) {
        email?.let { emailEnabled = it }
        push?.let { pushEnabled = it }
        inApp?.let { inAppEnabled = it }
        updatedAt = LocalDateTime.now()
    }

    /**
     * Creates a copy with updated preferences
     */
    fun copyWithUpdatedPreferences(
        email: Boolean = this.emailEnabled,
        push: Boolean = this.pushEnabled,
        inApp: Boolean = this.inAppEnabled
    ): UserNotificationPreference {
        return this.copy(
            emailEnabled = email,
            pushEnabled = push,
            inAppEnabled = inApp,
            updatedAt = LocalDateTime.now()
        )
    }

    override fun toString(): String {
        return "UserNotificationPreference(id=$id, userId=$userId, notificationType=$notificationType, " +
                "emailEnabled=$emailEnabled, pushEnabled=$pushEnabled, inAppEnabled=$inAppEnabled, " +
                "createdAt=$createdAt, updatedAt=$updatedAt)"
    }

    companion object {
        /**
         * Creates default preferences for a user with all channels enabled
         */
        fun createDefault(userId: Long, notificationType: NotificationType): UserNotificationPreference {
            return UserNotificationPreference(
                userId = userId,
                notificationType = notificationType,
                emailEnabled = true,
                pushEnabled = true,
                inAppEnabled = true
            )
        }

        /**
         * Creates preferences with all channels disabled
         */
        fun createDisabled(userId: Long, notificationType: NotificationType): UserNotificationPreference {
            return UserNotificationPreference(
                userId = userId,
                notificationType = notificationType,
                emailEnabled = false,
                pushEnabled = false,
                inAppEnabled = false
            )
        }

        /**
         * Creates preferences with only specific channels enabled
         */
        fun createWithChannels(
            userId: Long,
            notificationType: NotificationType,
            enabledChannels: List<NotificationChannel>
        ): UserNotificationPreference {
            return UserNotificationPreference(
                userId = userId,
                notificationType = notificationType,
                emailEnabled = NotificationChannel.EMAIL in enabledChannels,
                pushEnabled = NotificationChannel.PUSH in enabledChannels,
                inAppEnabled = NotificationChannel.IN_APP in enabledChannels
            )
        }
    }
}