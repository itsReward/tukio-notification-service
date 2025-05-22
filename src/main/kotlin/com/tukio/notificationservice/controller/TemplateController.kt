package com.tukio.notificationservice.controller

import com.tukio.notificationservice.dto.NotificationTemplateDTO
import com.tukio.notificationservice.dto.UpdateTemplateRequest
import com.tukio.notificationservice.model.NotificationChannel
import com.tukio.notificationservice.service.NotificationTemplateService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notification-templates")
class TemplateController(
    private val templateService: NotificationTemplateService
) {

    @GetMapping
    fun getAllTemplates(): ResponseEntity<List<NotificationTemplateDTO>> {
        return ResponseEntity.ok(templateService.getAllTemplates())
    }

    @GetMapping("/channel/{channel}")
    fun getTemplatesByChannel(@PathVariable channel: NotificationChannel): ResponseEntity<List<NotificationTemplateDTO>> {
        return ResponseEntity.ok(templateService.getTemplatesByChannel(channel))
    }

    @GetMapping("/{templateKey}")
    fun getTemplateByKey(@PathVariable templateKey: String): ResponseEntity<NotificationTemplateDTO> {
        return ResponseEntity.ok(templateService.getTemplateByKey(templateKey))
    }

    @PostMapping
    fun createTemplate(@RequestBody template: NotificationTemplateDTO): ResponseEntity<NotificationTemplateDTO> {
        val createdTemplate = templateService.createTemplate(template)
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTemplate)
    }

    @PutMapping("/{templateKey}")
    fun updateTemplate(
        @PathVariable templateKey: String,
        @RequestBody updateRequest: UpdateTemplateRequest
    ): ResponseEntity<NotificationTemplateDTO> {
        val updatedTemplate = templateService.updateTemplate(templateKey, updateRequest)
        return ResponseEntity.ok(updatedTemplate)
    }

    @DeleteMapping("/{templateKey}")
    fun deleteTemplate(@PathVariable templateKey: String): ResponseEntity<Void> {
        templateService.deleteTemplate(templateKey)
        return ResponseEntity.noContent().build()
    }
}