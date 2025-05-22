/*
package com.tukio.notificationservice.service

import com.tukio.notificationservice.model.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NotificationModelTest {

    @Test
    fun `test notification creation`() {
        // Given
        val notification = Notification(
            userId = 1L,
            title = "Test Notification",
            content = "Test Content",
            notificationType = NotificationType.EVENT_REMINDER,
            channel = NotificationChannel.EMAIL,
            status = NotificationStatus.PENDING
        )

        // Then
        assertEquals(1L, notification.userId)
        assertEquals("Test Notification", notification.title)
        assertEquals("Test Content", notification.content)
        assertEquals(NotificationType.EVENT_REMINDER, notification.notificationType)
        assertEquals(NotificationChannel.EMAIL, notification.channel)
        assertEquals(NotificationStatus.PENDING, notification.status)
        assertNotNull(notification.createdAt)
        assertNotNull(notification.updatedAt)
    }

    @Test
    fun `test notification template creation`() {
        // Given
        val template = NotificationTemplate(
            templateKey = "TEST_TEMPLATE",
            title = "Hello {{userName}}",
            content = "Welcome to {{eventName}}",
            channel = NotificationChannel.EMAIL,
            templateType = TemplateType.HTML
        )

        // Then
        assertEquals("TEST_TEMPLATE", template.templateKey)
        assertEquals("Hello {{userName}}", template.title)
        assertEquals("Welcome to {{eventName}}", template.content)
        assertEquals(NotificationChannel.EMAIL, template.channel)
        assertEquals(TemplateType.HTML, template.templateType)
        assertNotNull(template.createdAt)
        assertNotNull(template.updatedAt)
    }

    @Test
    fun `test user notification preference creation`() {
        // Given
        val preference = UserNotificationPreference(
            userId = 1L,
            notificationType = NotificationType.EVENT_REMINDER,
            emailEnabled = true,
            pushEnabled = false,
            inAppEnabled = true
        )

        // Then
        assertEquals(1L, preference.userId)
        assertEquals(NotificationType.EVENT_REMINDER, preference.notificationType)
        assertEquals(true, preference.emailEnabled)
        assertEquals(false, preference.pushEnabled)
        assertEquals(true, preference.inAppEnabled)
        assertNotNull(preference.createdAt)
        assertNotNull(preference.updatedAt)
    }

    @Test
    fun `test delivery attempt creation`() {
        // Given
        val attempt = NotificationDeliveryAttempt(
            notificationId = 1L,
            attemptNumber = 1,
            status = DeliveryStatus.SUCCESS
        )

        // Then
        assertEquals(1L, attempt.notificationId)
        assertEquals(1, attempt.attemptNumber)
        assertEquals(DeliveryStatus.SUCCESS, attempt.status)
        assertNotNull(attempt.attemptedAt)
    }

    @Test
    fun `test simple template variable replacement`() {
        // Given
        val template = "Hello {{userName}}, welcome to {{eventName}}"
        val variables = mapOf(
            "userName" to "John Doe",
            "eventName" to "Spring Boot Workshop"
        )

        // When
        var result = template
        for ((key, value) in variables) {
            result = result.replace("{{$key}}", value)
        }

        // Then
        assertEquals("Hello John Doe, welcome to Spring Boot Workshop", result)
    }

    @Test
    fun `test notification status transitions`() {
        // Test that we can move through different notification statuses
        val statuses = listOf(
            NotificationStatus.PENDING,
            NotificationStatus.SENT,
            NotificationStatus.DELIVERED,
            NotificationStatus.READ
        )

        // Verify all statuses are valid
        statuses.forEach { status ->
            assertNotNull(status)
            assertNotNull(status.name)
        }
    }

    @Test
    fun `test notification channels`() {
        // Test all notification channels
        val channels = listOf(
            NotificationChannel.EMAIL,
            NotificationChannel.PUSH,
            NotificationChannel.IN_APP
        )

        // Verify all channels are valid
        channels.forEach { channel ->
            assertNotNull(channel)
            assertNotNull(channel.name)
        }
    }
}*/
