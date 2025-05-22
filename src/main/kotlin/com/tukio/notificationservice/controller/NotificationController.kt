package com.tukio.notificationservice.controller

import com.tukio.notificationservice.dto.NotificationRequestDTO
import com.tukio.notificationservice.dto.NotificationResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notifications")
class NotificationController {
    
    @PostMapping
    fun createNotification(@RequestBody request: NotificationRequestDTO): ResponseEntity<List<NotificationResponseDTO>> {
        // TODO: Implement notification creation
        return ResponseEntity.status(HttpStatus.CREATED).body(emptyList())
    }
    
    @GetMapping("/user/{userId}")
    fun getUserNotifications(@PathVariable userId: Long): ResponseEntity<List<NotificationResponseDTO>> {
        // TODO: Implement get user notifications
        return ResponseEntity.ok(emptyList())
    }
    
    @GetMapping("/health")
    fun health(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("status" to "UP"))
    }
}
