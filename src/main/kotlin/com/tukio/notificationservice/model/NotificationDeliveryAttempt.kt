package com.tukio.notificationservice.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entity representing a delivery attempt for a notification
 */
@Entity
@Table(
    name = "notification_delivery_attempts",
    indexes = [
        Index(name = "idx_delivery_attempts_notification_id", columnList = "notification_id"),
        Index(name = "idx_delivery_attempts_status", columnList = "status")
    ]
)
data class NotificationDeliveryAttempt(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "notification_id", nullable = false)
    val notificationId: Long,

    @Column(name = "attempt_number", nullable = false)
    val attemptNumber: Int,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    val status: DeliveryStatus,

    @Column(name = "error_message", columnDefinition = "TEXT")
    val errorMessage: String? = null,

    @Column(name = "attempted_at", nullable = false, updatable = false)
    val attemptedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Checks if this attempt was successful
     */
    fun isSuccessful(): Boolean = status == DeliveryStatus.SUCCESS

    /**
     * Checks if this attempt failed
     */
    fun isFailed(): Boolean = status == DeliveryStatus.FAILED

    /**
     * Gets a human-readable description of the attempt
     */
    fun getDescription(): String {
        return "Attempt #$attemptNumber - $status" +
                if (errorMessage != null) " ($errorMessage)" else ""
    }

    override fun toString(): String {
        return "NotificationDeliveryAttempt(id=$id, notificationId=$notificationId, " +
                "attemptNumber=$attemptNumber, status=$status, attemptedAt=$attemptedAt)"
    }

    companion object {
        /**
         * Creates a successful delivery attempt
         */
        fun successful(notificationId: Long, attemptNumber: Int): NotificationDeliveryAttempt {
            return NotificationDeliveryAttempt(
                notificationId = notificationId,
                attemptNumber = attemptNumber,
                status = DeliveryStatus.SUCCESS
            )
        }

        /**
         * Creates a failed delivery attempt
         */
        fun failed(
            notificationId: Long,
            attemptNumber: Int,
            errorMessage: String? = null
        ): NotificationDeliveryAttempt {
            return NotificationDeliveryAttempt(
                notificationId = notificationId,
                attemptNumber = attemptNumber,
                status = DeliveryStatus.FAILED,
                errorMessage = errorMessage
            )
        }

        /**
         * Creates a retry attempt
         */
        fun retry(notificationId: Long, attemptNumber: Int): NotificationDeliveryAttempt {
            return NotificationDeliveryAttempt(
                notificationId = notificationId,
                attemptNumber = attemptNumber,
                status = DeliveryStatus.RETRY
            )
        }
    }
}

/**
 * Enum representing the status of a notification delivery attempt
 */
enum class DeliveryStatus {
    /**
     * The delivery attempt was successful
     */
    SUCCESS,

    /**
     * The delivery attempt failed
     */
    FAILED,

    /**
     * The delivery attempt will be retried
     */
    RETRY,

    /**
     * The delivery attempt is in progress
     */
    IN_PROGRESS;

    /**
     * Checks if this status indicates success
     */
    fun isSuccess(): Boolean = this == SUCCESS

    /**
     * Checks if this status indicates failure
     */
    fun isFailure(): Boolean = this == FAILED

    /**
     * Checks if this status indicates the attempt can be retried
     */
    fun canRetry(): Boolean = this == RETRY || this == FAILED

    /**
     * Gets a human-readable description
     */
    fun getDescription(): String {
        return when (this) {
            SUCCESS -> "Successfully delivered"
            FAILED -> "Delivery failed"
            RETRY -> "Scheduled for retry"
            IN_PROGRESS -> "Delivery in progress"
        }
    }
}