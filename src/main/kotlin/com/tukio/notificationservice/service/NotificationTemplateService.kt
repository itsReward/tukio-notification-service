package com.tukio.notificationservice.service

import com.tukio.notificationservice.dto.NotificationTemplateDTO
import com.tukio.notificationservice.dto.UpdateTemplateRequest
import com.tukio.notificationservice.exception.NotFoundException
import com.tukio.notificationservice.exception.ValidationException
import com.tukio.notificationservice.model.NotificationChannel
import com.tukio.notificationservice.model.NotificationTemplate
import com.tukio.notificationservice.model.TemplateType
import com.tukio.notificationservice.repository.NotificationTemplateRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Service for managing notification templates
 */
@Service
class NotificationTemplateService(
    private val templateRepository: NotificationTemplateRepository
) {
    private val logger = LoggerFactory.getLogger(NotificationTemplateService::class.java)

    /**
     * Get all notification templates
     */
    fun getAllTemplates(): List<NotificationTemplateDTO> {
        return templateRepository.findAll().map { it.toDTO() }
    }

    /**
     * Get templates by channel
     */
    fun getTemplatesByChannel(channel: NotificationChannel): List<NotificationTemplateDTO> {
        return templateRepository.findByChannel(channel).map { it.toDTO() }
    }

    /**
     * Get a template by key
     */
    fun getTemplateByKey(templateKey: String): NotificationTemplateDTO {
        val template = templateRepository.findByTemplateKey(templateKey)
            .orElseThrow { NotFoundException("Template not found with key: $templateKey") }

        return template.toDTO()
    }

    /**
     * Create a new notification template
     */
    @Transactional
    fun createTemplate(templateDTO: NotificationTemplateDTO): NotificationTemplateDTO {
        // Check if template key already exists
        if (templateRepository.findByTemplateKey(templateDTO.templateKey).isPresent) {
            throw ValidationException("Template with key '${templateDTO.templateKey}' already exists")
        }

        // Validate template content based on type
        validateTemplateContent(templateDTO.content, templateDTO.templateType)

        val template = NotificationTemplate(
            templateKey = templateDTO.templateKey,
            title = templateDTO.title,
            content = templateDTO.content,
            description = templateDTO.description,
            channel = templateDTO.channel,
            templateType = templateDTO.templateType
        )

        val savedTemplate = templateRepository.save(template)
        logger.info("Created new template: ${savedTemplate.templateKey}")

        return savedTemplate.toDTO()
    }

    /**
     * Update an existing template
     */
    @Transactional
    fun updateTemplate(templateKey: String, updateRequest: UpdateTemplateRequest): NotificationTemplateDTO {
        val template = templateRepository.findByTemplateKey(templateKey)
            .orElseThrow { NotFoundException("Template not found with key: $templateKey") }

        // Update fields if provided
        updateRequest.title?.let {
            template.title = it
        }

        updateRequest.content?.let {
            validateTemplateContent(it, template.templateType)
            template.content = it
        }

        updateRequest.description?.let {
            template.description = it
        }

        template.updatedAt = LocalDateTime.now()

        val updatedTemplate = templateRepository.save(template)
        logger.info("Updated template: ${updatedTemplate.templateKey}")

        return updatedTemplate.toDTO()
    }

    /**
     * Delete a template
     */
    @Transactional
    fun deleteTemplate(templateKey: String) {
        val template = templateRepository.findByTemplateKey(templateKey)
            .orElseThrow { NotFoundException("Template not found with key: $templateKey") }

        templateRepository.delete(template)
        logger.info("Deleted template: $templateKey")
    }

    /**
     * Validate template content based on type
     */
    private fun validateTemplateContent(content: String, templateType: TemplateType) {
        when (templateType) {
            TemplateType.HTML -> {
                if (!content.contains("<")) {
                    throw ValidationException("HTML template must contain HTML tags")
                }
            }
            TemplateType.TEXT -> {
                // No specific validation for text templates
            }
            TemplateType.PUSH -> {
                // Push notifications should be short
                if (content.length > 200) {
                    throw ValidationException("Push notification content should not exceed 200 characters")
                }
            }
        }
    }

    // Extension function to convert NotificationTemplate to NotificationTemplateDTO
    private fun NotificationTemplate.toDTO(): NotificationTemplateDTO {
        return NotificationTemplateDTO(
            id = this.id,
            templateKey = this.templateKey,
            title = this.title,
            content = this.content,
            description = this.description,
            channel = this.channel,
            templateType = this.templateType
        )
    }
}