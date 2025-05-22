package com.tukio.notificationservice.repository

import com.tukio.notificationservice.model.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.Optional

@Repository
interface NotificationTemplateRepository : JpaRepository<NotificationTemplate, Long> {
    fun findByTemplateKey(templateKey: String): Optional<NotificationTemplate>

    fun findByChannel(channel: NotificationChannel): List<NotificationTemplate>
}

@Repository
interface UserNotificationPreferenceRepository : JpaRepository<UserNotificationPreference, Long> {
    fun findByUserId(userId: Long): List<UserNotificationPreference>

    fun findByUserIdAndNotificationType(userId: Long, notificationType: NotificationType): Optional<UserNotificationPreference>

    @Query("""
        SELECT p FROM UserNotificationPreference p
        WHERE p.userId = :userId AND
        (
            (p.notificationType = :notificationType) OR 
            (:notificationType IS NULL)
        )
    """)
    fun findUserPreferences(
        @Param("userId") userId: Long,
        @Param("notificationType") notificationType: NotificationType?
    ): List<UserNotificationPreference>
}

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByUserId(userId: Long, pageable: Pageable): Page<Notification>

    fun findByUserIdAndStatus(userId: Long, status: NotificationStatus, pageable: Pageable): Page<Notification>

    fun findByUserIdAndChannel(userId: Long, channel: NotificationChannel, pageable: Pageable): Page<Notification>

    @Query("""
        SELECT n FROM Notification n
        WHERE n.userId = :userId AND
        n.status = :status AND
        n.channel = :channel
    """)
    fun findByUserIdAndStatusAndChannel(
        @Param("userId") userId: Long,
        @Param("status") status: NotificationStatus,
        @Param("channel") channel: NotificationChannel,
        pageable: Pageable
    ): Page<Notification>

    @Query("""
        SELECT n FROM Notification n
        WHERE n.status = :status AND
        n.channel = :channel
        ORDER BY n.createdAt ASC
    """)
    fun findByStatusAndChannelOrderByCreatedAt(
        @Param("status") status: NotificationStatus,
        @Param("channel") channel: NotificationChannel,
        pageable: Pageable
    ): Page<Notification>

    @Query("""
        SELECT n FROM Notification n
        WHERE n.referenceType = :referenceType AND
        n.referenceId = :referenceId
    """)
    fun findByReference(
        @Param("referenceType") referenceType: String,
        @Param("referenceId") referenceId: String,
        pageable: Pageable
    ): Page<Notification>

    @Query("""
        SELECT COUNT(n) FROM Notification n
        WHERE n.userId = :userId AND
        n.status = :status AND
        n.channel = :channel
    """)
    fun countByUserIdAndStatusAndChannel(
        @Param("userId") userId: Long,
        @Param("status") status: NotificationStatus,
        @Param("channel") channel: NotificationChannel
    ): Long

    @Query("""
        SELECT COUNT(n) FROM Notification n
        WHERE n.userId = :userId AND
        n.status = com.tukio.notificationservice.model.NotificationStatus.DELIVERED AND
        n.channel = com.tukio.notificationservice.model.NotificationChannel.IN_APP AND
        n.readAt IS NULL
    """)
    fun countUnreadInAppNotifications(@Param("userId") userId: Long): Long
}

@Repository
interface NotificationDeliveryAttemptRepository : JpaRepository<NotificationDeliveryAttempt, Long> {
    fun findByNotificationId(notificationId: Long): List<NotificationDeliveryAttempt>

    fun countByNotificationIdAndStatus(notificationId: Long, status: DeliveryStatus): Int
}