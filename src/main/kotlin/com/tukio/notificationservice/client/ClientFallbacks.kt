package com.tukio.notificationservice.client

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Fallback implementation for UserServiceClient when the user service is unavailable
 */
@Component
class UserServiceClientFallback : UserServiceClient {

    private val logger = LoggerFactory.getLogger(UserServiceClientFallback::class.java)

    override fun getUserById(userId: Long): UserDTO {
        logger.warn("Fallback: getUserById for userId $userId")
        return UserDTO(
            id = userId,
            username = "unknown_user",
            email = "unknown@example.com",
            firstName = "Unknown",
            lastName = "User",
            profilePictureUrl = null,
            pushToken = null
        )
    }

    override fun getUsersByIds(userIds: List<Long>): List<UserDTO> {
        logger.warn("Fallback: getUsersByIds for ${userIds.size} users")
        return userIds.map {
            UserDTO(
                id = it,
                username = "unknown_user",
                email = "unknown@example.com",
                firstName = "Unknown",
                lastName = "User",
                profilePictureUrl = null,
                pushToken = null
            )
        }
    }
}

/**
 * Fallback implementation for EventServiceClient when the event service is unavailable
 */
@Component
class EventServiceClientFallback : EventServiceClient {

    private val logger = LoggerFactory.getLogger(EventServiceClientFallback::class.java)

    override fun getEventById(eventId: Long): EventDTO {
        logger.warn("Fallback: getEventById for eventId $eventId")
        return EventDTO(
            id = eventId,
            title = "Unknown Event",
            description = "Event details unavailable",
            startTime = "",
            endTime = "",
            location = "Unknown",
            venueId = null,
            venueName = null
        )
    }

    override fun getUpcomingEvents(): List<EventDTO> {
        logger.warn("Fallback: getUpcomingEvents")
        return emptyList()
    }
}