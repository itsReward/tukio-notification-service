package com.tukio.notificationservice.dto

import com.tukio.notificationservice.model.NotificationChannel
import com.tukio.notificationservice.model.TemplateType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * DTO for notification template
 */
data class NotificationTemplateDTO(
    val id: Long? = null,

    @field:NotBlank(message = "Template key is required")
    @field:Size(max = 100, message = "Template key must not exceed 100 characters")
    val templateKey: String,

    @field:NotBlank(message = "Title is required")
    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    val title: String,

    @field:NotBlank(message = "Content is required")
    val content: String,

    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    val description: String? = null,

    @field:NotNull(message = "Channel is required")
    val channel: NotificationChannel,

    @field:NotNull(message = "Template type is required")
    val templateType: TemplateType
)