package com.tukio.notificationservice.service

import com.tukio.notificationservice.client.EventServiceClient
import com.tukio.notificationservice.client.UserServiceClient
import com.tukio.notificationservice.dto.NotificationRequestDTO
import com.tukio.notificationservice.dto.NotificationResponseDTO
import com.tukio.notificationservice.model.Notification
import com.tukio.notificationservice.model.NotificationChannel
import com.tukio.notificationservice.model.NotificationStatus
import com.tukio.notificationservice.model.NotificationType
import com.tukio.notificationservice.model.NotificationTemplate
import com.tukio.notificationservice.model.TemplateType
import com.tukio.notificationservice.model.UserNotificationPreference
import com.tukio.notificationservice.repository.NotificationRepository
import com.tukio.notificationservice.repository.NotificationTemplateRepository
import com.tukio.notificationservice.repository.UserNotificationPreferenceRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.thymeleaf.TemplateEngine
import java.util.Optional
import org.junit.jupiter.api.Assertions.*
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {

    @Mock
    private lateinit var notificationRepository: NotificationRepository

    @Mock
    private lateinit var templateRepository: NotificationTemplateRepository

    @Mock
    private lateinit var preferenceRepository: UserNotificationPreferenceRepository

    @Mock
    private lateinit var userServiceClient: UserServiceClient

    @Mock
    private lateinit var eventServiceClient: EventServiceClient

    @Mock
    private lateinit var emailSender: EmailSenderService

    @Mock
    private lateinit var pushSender: PushNotificationService

    @Mock
    private lateinit var inAppSender: InAppNotificationService

    @Mock
    private lateinit var templateEngine: TemplateEngine

    private lateinit var notificationService: NotificationServiceImpl

    @BeforeEach
    fun setup() {
        notificationService = NotificationServiceImpl(
            notificationRepository,
            templateRepository,
            preferenceRepository,
            userServiceClient,
            eventServiceClient,
            emailSender,
            pushSender,
            inAppSender
        )
    }

    @Test
    fun `test createNotification with valid request`() {
        // Given
        val userId = 1L
        val request = NotificationRequestDTO(
            userId = userId,
            templateKey = "EVENT_REGISTRATION_CONFIRMATION_EMAIL",
            templateData = mapOf(
                "eventName" to "Test Event",
                "userName" to "John Doe",
                "eventDate" to "2025-06-01",
                "eventTime" to "10:00 AM",
                "eventLocation" to "Test Venue"
            ),
            channels = listOf(NotificationChannel.EMAIL),
            notificationType = NotificationType.EVENT_REGISTRATION
        )

        val preference = UserNotificationPreference(
            userId = userId,
            notificationType = NotificationType.EVENT_REGISTRATION,
            emailEnabled = true
        )

        val template = NotificationTemplate(
            id = 1,
            templateKey = "EVENT_REGISTRATION_CONFIRMATION_EMAIL",
            title = "Registration Confirmed: {{eventName}}",
            content = "Hello {{userName}}, your registration for {{eventName}} is confirmed.",
            channel = NotificationChannel.EMAIL,
            templateType = TemplateType.HTML
        )

        val savedNotification = Notification(
            id = 1,
            userId = userId,
            title = "Registration Confirmed: Test Event",
            content = "Hello John Doe, your registration for Test Event is confirmed.",
            notificationType = NotificationType.EVENT_REGISTRATION,
            channel = NotificationChannel.EMAIL,
            status = NotificationStatus.PENDING
        )

        // Mock repository responses
        `when`(preferenceRepository.findByUserIdAndNotificationType(userId, NotificationType.EVENT_REGISTRATION))
            .thenReturn(Optional.of(preference))

        `when`(templateRepository.findByTemplateKey("EVENT_REGISTRATION_CONFIRMATION_EMAIL"))
            .thenReturn(Optional.of(template))

        `when`(notificationRepository.save(any()))
            .thenReturn(savedNotification)

        `when`(emailSender.send(any()))
            .thenReturn(true)

        // When
        val result = notificationService.createNotification(request)

        // Then
        assertNotNull(result)
        assertEquals(1, result.size)
        assertEquals(userId, result[0].userId)
        assertEquals("Registration Confirmed: Test Event", result[0].title)
        assertEquals(NotificationType.EVENT_REGISTRATION, result[0].notificationType)
        assertEquals(NotificationChannel.EMAIL, result[0].channel)
    }

    @Test
    fun `test getUserNotifications returns correct notifications`() {
        // Given
        val userId = 1L
        val pageable = Pageable.ofSize(10)

        val notifications = listOf(
            Notification(
                id = 1,
                userId = userId,
                title = "Test Notification 1",
                content = "Test Content 1",
                notificationType = NotificationType.EVENT_REMINDER,
                channel = NotificationChannel.EMAIL,
                status = NotificationStatus.SENT
            ),
            Notification(
                id = 2,
                userId = userId,
                title = "Test Notification 2",
                content = "Test Content 2",
                notificationType = NotificationType.EVENT_REGISTRATION,
                channel = NotificationChannel.IN_APP,
                status = NotificationStatus.DELIVERED
            )
        )

        val page = PageImpl(notifications)

        // Mock repository response
        `when`(notificationRepository.findByUserId(userId, pageable))
            .thenReturn(page)

        // When
        val result = notificationService.getUserNotifications(userId, null, null, pageable)

        // Then
        assertNotNull(result)
        assertEquals(2, result.totalElements)
        assertEquals("Test Notification 1", result.content[0].title)
        assertEquals("Test Notification 2", result.content[1].title)
    }

    @Test
    fun `test markAsRead updates notification status correctly`() {
        // Given
        val notificationId = 1L
        val userId = 1L

        val notification = Notification(
            id = notificationId,
            userId = userId,
            title = "Test Notification",
            content = "Test Content",
            notificationType = NotificationType.EVENT_REMINDER,
            channel = NotificationChannel.IN_APP,
            status = NotificationStatus.DELIVERED
        )

        val updatedNotification = notification.copy(
            readAt = LocalDateTime.now(),
            status = NotificationStatus.READ
        )

        // Mock repository responses
        `when`(notificationRepository.findById(notificationId))
            .thenReturn(Optional.of(notification))

        `when`(notificationRepository.save(any()))
            .thenReturn(updatedNotification)

        // When
        val result = notificationService.markAsRead(notificationId, userId)

        // Then
        assertNotNull(result)
        assertEquals(notificationId, result.id)
        assertEquals(userId, result.userId)
        assertNotNull(result.readAt)
    }
}