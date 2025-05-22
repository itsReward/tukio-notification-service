package com.tukio.notificationservice.service

import com.tukio.notificationservice.dto.NotificationPreferenceDTO
import com.tukio.notificationservice.dto.UpdatePreferenceRequest
import com.tukio.notificationservice.exception.NotFoundException
import com.tukio.notificationservice.model.NotificationType
import com.tukio.notificationservice.model.UserNotificationPreference
import com.tukio.notificationservice.repository.UserNotificationPreferenceRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import kotlin.collections.map

/**
 * Service for managing user notification preferences
 */
@Service
class NotificationPreferenceService(
    private val preferenceRepository: UserNotificationPreferenceRepository
) {
    private val logger = LoggerFactory.getLogger(NotificationPreferenceService::class.java)

    /**
     * Get all notification preferences for a user
     */
    fun getUserPreferences(userId: Long): List<NotificationPreferenceDTO> {
        return preferenceRepository.findByUserId(userId).map { it.toDTO() }
    }

    /**
     * Get a specific notification preference
     */
    fun getUserPreference(userId: Long, notificationType: NotificationType): NotificationPreferenceDTO {
        val preference = preferenceRepository.findByUserIdAndNotificationType(userId, notificationType)
            .orElseThrow { NotFoundException("Preference not found for user $userId and type $notificationType") }

        return preference.toDTO()
    }

    /**
     * Create or update a notification preference
     */
    @Transactional
    fun createOrUpdatePreference(preferenceDTO: NotificationPreferenceDTO): NotificationPreferenceDTO {
        // Check if preference already exists
        val existingPreference = preferenceRepository.findByUserIdAndNotificationType(
            preferenceDTO.userId, preferenceDTO.notificationType
        )

        if (existingPreference.isPresent) {
            val preference = existingPreference.get()

            // Update existing preference
            preference.emailEnabled = preferenceDTO.emailEnabled
            preference.pushEnabled = preferenceDTO.pushEnabled
            preference.inAppEnabled = preferenceDTO.inAppEnabled
            preference.updatedAt = LocalDateTime.now()

            val updatedPreference = preferenceRepository.save(preference)
            logger.info("Updated preference for user ${preference.userId} and type ${preference.notificationType}")

            return updatedPreference.toDTO()
        } else {
            // Create new preference
            val preference = UserNotificationPreference(
                userId = preferenceDTO.userId,
                notificationType = preferenceDTO.notificationType,
                emailEnabled = preferenceDTO.emailEnabled,
                pushEnabled = preferenceDTO.pushEnabled,
                inAppEnabled = preferenceDTO.inAppEnabled
            )

            val savedPreference = preferenceRepository.save(preference)
            logger.info("Created new preference for user ${preference.userId} and type ${preference.notificationType}")

            return savedPreference.toDTO()
        }
    }

    /**
     * Update an existing preference
     */
    @Transactional
    fun updatePreference(
        userId: Long,
        notificationType: NotificationType,
        updateRequest: UpdatePreferenceRequest
    ): NotificationPreferenceDTO {
        val preference = preferenceRepository.findByUserIdAndNotificationType(userId, notificationType)
            .orElseGet {
                // Create default preference if it doesn't exist
                UserNotificationPreference(
                    userId = userId,
                    notificationType = notificationType
                )
            }

        // Update fields if provided
        updateRequest.emailEnabled?.let { preference.emailEnabled = it }
        updateRequest.pushEnabled?.let { preference.pushEnabled = it }
        updateRequest.inAppEnabled?.let { preference.inAppEnabled = it }

        preference.updatedAt = LocalDateTime.now()

        val updatedPreference = preferenceRepository.save(preference)
        logger.info("Updated preference for user $userId and type $notificationType")

        return updatedPreference.toDTO()
    }

    /**
     * Delete a user's preference for a notification type
     */
    @Transactional
    fun deletePreference(userId: Long, notificationType: NotificationType) {
        val preference = preferenceRepository.findByUserIdAndNotificationType(userId, notificationType)
            .orElseThrow { NotFoundException("Preference not found for user $userId and type $notificationType") }

        preferenceRepository.delete(preference)
        logger.info("Deleted preference for user $userId and type $notificationType")
    }

    /**
     * Initialize default preferences for a new user
     */
    @Transactional
    fun initializeDefaultPreferences(userId: Long): List<NotificationPreferenceDTO> {
        val defaultPreferences = mutableListOf<UserNotificationPreference>()

        // Create default preferences for all notification types
        NotificationType.values().forEach { type ->
            val preference = UserNotificationPreference(
                userId = userId,
                notificationType = type,
                emailEnabled = true,
                pushEnabled = true,
                inAppEnabled = true
            )

            defaultPreferences.add(preference)
        }

        val savedPreferences = preferenceRepository.saveAll(defaultPreferences)
        logger.info("Initialized default preferences for user $userId")

        return savedPreferences.map { it.toDTO() }
    }

    // Extension function to convert UserNotificationPreference to NotificationPreferenceDTO
    private fun UserNotificationPreference.toDTO(): NotificationPreferenceDTO {
        return NotificationPreferenceDTO(
            userId = this.userId,
            notificationType = this.notificationType,
            emailEnabled = this.emailEnabled,
            pushEnabled = this.pushEnabled,
            inAppEnabled = this.inAppEnabled
        )
    }
}