package com.geekplay.moderationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekplay.moderationservice.dto.BanNotificationResponse;
import com.geekplay.moderationservice.dto.ModerationRequest;
import com.geekplay.moderationservice.model.BanNotification;
import com.geekplay.moderationservice.service.ModerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(ModerationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ModerationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ModerationService moderationService;

    private BanNotification banNotification;
    private ModerationRequest moderationRequest;

    @BeforeEach
    void setUp() {
        banNotification = new BanNotification();
        banNotification.setId(1L);
        banNotification.setUserId(1L);
        banNotification.setReason("Contenido inapropiado");
        banNotification.setDuration("30 minutos");
        banNotification.setAppealGuide("Contacta a soporte@geekplay.cl");
        banNotification.setTimestamp(System.currentTimeMillis());

        moderationRequest = new ModerationRequest();
        moderationRequest.setUserId(1L);
        moderationRequest.setContentId("post-123");
        moderationRequest.setReason("Spam");
        moderationRequest.setDurationMinutes(60);
        moderationRequest.setType("POST");
    }

    @Test
    void getNotifications_Success() throws Exception {
        List<BanNotification> notifications = Arrays.asList(banNotification);
        when(moderationService.getNotificationsByUserId(1L)).thenReturn(notifications);

        mockMvc.perform(get("/api/moderation/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].reason").value("Contenido inapropiado"))
                .andExpect(jsonPath("$[0].duration").value("30 minutos"));

        verify(moderationService).getNotificationsByUserId(1L);
    }

    @Test
    void getNotifications_EmptyList() throws Exception {
        when(moderationService.getNotificationsByUserId(999L)).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/moderation/notifications/user/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(moderationService).getNotificationsByUserId(999L);
    }

    @Test
    void performAction_Success() throws Exception {
        doNothing().when(moderationService).executeModeration(
            anyLong(), anyString(), anyString(), anyInt(), anyString()
        );

        mockMvc.perform(post("/api/moderation/action")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moderationRequest)))
                .andExpect(status().isAccepted());

        verify(moderationService).executeModeration(
            1L, "post-123", "Spam", 60, "POST"
        );
    }

    @Test
    void performAction_PostType() throws Exception {
        moderationRequest.setType("POST");
        doNothing().when(moderationService).executeModeration(
            anyLong(), anyString(), anyString(), anyInt(), anyString()
        );

        mockMvc.perform(post("/api/moderation/action")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moderationRequest)))
                .andExpect(status().isAccepted());

        verify(moderationService).executeModeration(1L, "post-123", "Spam", 60, "POST");
    }

    @Test
    void performAction_CommentType() throws Exception {
        moderationRequest.setType("COMMENT");
        moderationRequest.setContentId("comment-456");
        doNothing().when(moderationService).executeModeration(
            anyLong(), anyString(), anyString(), anyInt(), anyString()
        );

        mockMvc.perform(post("/api/moderation/action")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moderationRequest)))
                .andExpect(status().isAccepted());

        verify(moderationService).executeModeration(1L, "comment-456", "Spam", 60, "COMMENT");
    }

    @Test
    void deleteNotification_Success() throws Exception {
        doNothing().when(moderationService).deleteNotification(1L);

        mockMvc.perform(delete("/api/moderation/notifications/1"))
                .andExpect(status().isNoContent());

        verify(moderationService).deleteNotification(1L);
    }

    @Test
    void deleteNotification_CallsService() throws Exception {
        doNothing().when(moderationService).deleteNotification(anyLong());

        mockMvc.perform(delete("/api/moderation/notifications/123"))
                .andExpect(status().isNoContent());

        verify(moderationService).deleteNotification(123L);
    }
}
