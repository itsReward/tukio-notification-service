package com.tukio.notificationservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

/**
 * Client for the user service to fetch user details
 */
@FeignClient(name = "tukio-user-service", fallback = UserServiceClientFallback::class)
interface UserServiceClient {

    @GetMapping("/api/users/{id}")
    fun getUserById(@PathVariable("id") userId: Long): UserDTO

    @GetMapping("/api/users/emails")
    fun getUsersByIds(@RequestParam("ids") userIds: List<Long>): List<UserDTO>
}

/**
 * Client for the event service to fetch event details
 */
@FeignClient(name = "tukio-events-service", fallback = EventServiceClientFallback::class)
interface EventServiceClient {

    @GetMapping("/api/events/{id}")
    fun getEventById(@PathVariable("id") eventId: Long): EventDTO

    @GetMapping("/api/events/upcoming")
    fun getUpcomingEvents(): List<EventDTO>
}

/**
 * Data classes for client responses
 */
data class UserDTO(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val profilePictureUrl: String?,
    val pushToken: String?
)

data class EventDTO(
    val id: Long,
    val title: String,
    val description: String,
    val startTime: String,
    val endTime: String,
    val location: String,
    val venueId: Long?,
    val venueName: String?
)