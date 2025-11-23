package com.geekplay.moderationservice.service;

import com.geekplay.moderationservice.model.BanNotification;
import com.geekplay.moderationservice.repository.BanNotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ModerationServiceTest {

    @Mock
    private BanNotificationRepository notificationRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestBodySpec requestBodySpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ModerationService moderationService;

    private BanNotification testNotification;

    @BeforeEach
    void setUp() {
        // Inyectar las URLs de los servicios
        ReflectionTestUtils.setField(moderationService, "userServiceUrl", "http://localhost:8083/api/users");
        ReflectionTestUtils.setField(moderationService, "contentServiceUrl", "http://localhost:8081/api/posts");
        ReflectionTestUtils.setField(moderationService, "interactionServiceUrl", "http://localhost:8082/api/interactions");

        testNotification = new BanNotification();
        testNotification.setId(1L);
        testNotification.setUserId(1L);
        testNotification.setReason("Contenido inapropiado");
        testNotification.setDuration("30 minutos");
        testNotification.setAppealGuide("Contacta a soporte@geekplay.cl");
        testNotification.setTimestamp(System.currentTimeMillis());
    }

    @Test
    void getNotificationsByUserId_Success() {
        List<BanNotification> notifications = Arrays.asList(testNotification);
        when(notificationRepository.findByUserIdOrderByTimestampDesc(1L)).thenReturn(notifications);

        List<BanNotification> result = moderationService.getNotificationsByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getUserId());
        assertEquals("Contenido inapropiado", result.get(0).getReason());
        verify(notificationRepository).findByUserIdOrderByTimestampDesc(1L);
    }

    @Test
    void getNotificationsByUserId_EmptyList() {
        when(notificationRepository.findByUserIdOrderByTimestampDesc(999L)).thenReturn(Arrays.asList());

        List<BanNotification> result = moderationService.getNotificationsByUserId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void executeModeration_WithBanDuration() {
        // Mock WebClient para PUT (banear usuario)
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyLong())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        // Mock WebClient para DELETE (eliminar post)
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(notificationRepository.save(any(BanNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        moderationService.executeModeration(1L, "post-123", "Spam", 60, "POST");

        ArgumentCaptor<BanNotification> captor = ArgumentCaptor.forClass(BanNotification.class);
        verify(notificationRepository).save(captor.capture());

        BanNotification saved = captor.getValue();
        assertEquals(1L, saved.getUserId());
        assertTrue(saved.getReason().contains("Spam"));
        assertEquals("60 minutos", saved.getDuration());
    }

    @Test
    void executeModeration_PermanentBan() {
        // Mock WebClient para PUT
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyLong())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        // Mock WebClient para DELETE
        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(notificationRepository.save(any(BanNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        moderationService.executeModeration(1L, "post-123", "Violación grave", 0, "POST");

        ArgumentCaptor<BanNotification> captor = ArgumentCaptor.forClass(BanNotification.class);
        verify(notificationRepository).save(captor.capture());

        BanNotification saved = captor.getValue();
        assertEquals("Permanente", saved.getDuration());
    }

    @Test
    void executeModeration_PostType() {
        // Mock WebClient
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyLong())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(notificationRepository.save(any(BanNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        moderationService.executeModeration(1L, "post-123", "Spam", 30, "POST");

        ArgumentCaptor<BanNotification> captor = ArgumentCaptor.forClass(BanNotification.class);
        verify(notificationRepository).save(captor.capture());

        assertTrue(captor.getValue().getReason().contains("publicación"));
    }

    @Test
    void executeModeration_CommentType() {
        // Mock WebClient
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyLong())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(notificationRepository.save(any(BanNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        moderationService.executeModeration(1L, "comment-456", "Acoso", 120, "COMMENT");

        ArgumentCaptor<BanNotification> captor = ArgumentCaptor.forClass(BanNotification.class);
        verify(notificationRepository).save(captor.capture());

        assertTrue(captor.getValue().getReason().contains("comentario"));
    }

    @Test
    void executeModeration_SavesNotification() {
        // Mock WebClient
        when(webClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyLong())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(anyLong())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        when(webClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        when(notificationRepository.save(any(BanNotification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        moderationService.executeModeration(1L, "post-123", "Test reason", 30, "POST");

        verify(notificationRepository).save(any(BanNotification.class));
    }

    @Test
    void deleteNotification_Success() {
        doNothing().when(notificationRepository).deleteById(1L);

        moderationService.deleteNotification(1L);

        verify(notificationRepository).deleteById(1L);
    }

    @Test
    void deleteNotification_CallsRepository() {
        doNothing().when(notificationRepository).deleteById(anyLong());

        moderationService.deleteNotification(123L);

        verify(notificationRepository).deleteById(123L);
    }
}
