package com.tukio.notificationservice.exception

import java.time.LocalDateTime

/**
 * Exception thrown when a resource is not found
 */
class NotFoundException(message: String) : RuntimeException(message)

/**
 * Exception thrown when validation fails
 */
class ValidationException(message: String) : RuntimeException(message)

/**
 * Exception thrown when a template processing error occurs
 */
class TemplateProcessingException(message: String) : RuntimeException(message)

/**
 * Exception thrown when notification sending fails
 */
class NotificationSendException(message: String, val channel: String) : RuntimeException(message)

/**
 * Error response DTO
 */
data class ErrorResponse(
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val status: Int,
    val error: String,
    val message: String,
    val path: String
)