package com.tukio.notificationservice.dto

import jakarta.validation.constraints.Size

/**
 * DTO for updating a notification template
 */
data class UpdateTemplateRequest(
    @field:Size(max = 255, message = "Title must not exceed 255 characters")
    val title: String? = null,

    val content: String? = null,

    @field:Size(max = 255, message = "Description must not exceed 255 characters")
    val description: String? = null
) {
    /**
     * Validates that at least one field is provided for update
     */
    fun hasUpdates(): Boolean {
        return !title.isNullOrBlank() ||
                !content.isNullOrBlank() ||
                description != null
    }

    /**
     * Returns a map of non-null fields for logging purposes
     */
    fun getUpdatedFields(): Map<String, Any?> {
        val updates = mutableMapOf<String, Any?>()

        title?.let { updates["title"] = it }
        content?.let { updates["content"] = it }
        description?.let { updates["description"] = it }

        return updates
    }
}