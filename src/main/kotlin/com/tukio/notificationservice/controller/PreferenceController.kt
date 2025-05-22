package com.tukio.notificationservice.controller

import com.tukio.notificationservice.dto.NotificationPreferenceDTO
import com.tukio.notificationservice.dto.UpdatePreferenceRequest
import com.tukio.notificationservice.model.NotificationType
import com.tukio.notificationservice.service.NotificationPreferenceService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notification-preferences")
class PreferenceController(
    private val preferenceService: NotificationPreferenceService
) {

    @GetMapping("/user/{userId}")
    fun getUserPreferences(@PathVariable userId: Long): ResponseEntity<List<NotificationPreferenceDTO>> {
        return ResponseEntity.ok(preferenceService.getUserPreferences(userId))
    }

    @GetMapping("/user/{userId}/type/{notificationType}")
    fun getUserPreference(
        @PathVariable userId: Long,
        @PathVariable notificationType: NotificationType
    ): ResponseEntity<NotificationPreferenceDTO> {
        return ResponseEntity.ok(preferenceService.getUserPreference(userId, notificationType))
    }

    @PostMapping
    fun createPreference(@RequestBody preference: NotificationPreferenceDTO): ResponseEntity<NotificationPreferenceDTO> {
        val createdPreference = preferenceService.createOrUpdatePreference(preference)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPreference)
    }

    @PutMapping("/user/{userId}/type/{notificationType}")
    fun updatePreference(
        @PathVariable userId: Long,
        @PathVariable notificationType: NotificationType,
        @RequestBody updateRequest: UpdatePreferenceRequest
    ): ResponseEntity<NotificationPreferenceDTO> {
        val updatedPreference = preferenceService.updatePreference(userId, notificationType, updateRequest)
        return ResponseEntity.ok(updatedPreference)
    }

    @DeleteMapping("/user/{userId}/type/{notificationType}")
    fun deletePreference(
        @PathVariable userId: Long,
        @PathVariable notificationType: NotificationType
    ): ResponseEntity<Void> {
        preferenceService.deletePreference(userId, notificationType)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/user/{userId}/initialize")
    fun initializeDefaultPreferences(@PathVariable userId: Long): ResponseEntity<List<NotificationPreferenceDTO>> {
        val preferences = preferenceService.initializeDefaultPreferences(userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(preferences)
    }
}