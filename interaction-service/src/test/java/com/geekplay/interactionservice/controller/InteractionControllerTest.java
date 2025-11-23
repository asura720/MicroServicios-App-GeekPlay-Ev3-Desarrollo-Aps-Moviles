package com.geekplay.interactionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekplay.interactionservice.dto.CommentResponse;
import com.geekplay.interactionservice.dto.LikeResponse;
import com.geekplay.interactionservice.model.Comment;
import com.geekplay.interactionservice.service.InteractionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

@WebMvcTest(InteractionController.class)
@AutoConfigureMockMvc(addFilters = false)
class InteractionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InteractionService interactionService;

    private Comment testComment;
    private CommentResponse commentResponse;
    private LikeResponse likeResponse;

    @BeforeEach
    void setUp() {
        testComment = new Comment();
        testComment.setId("comment-1");
        testComment.setPostId(1L);
        testComment.setAuthorId(1L);
        testComment.setContent("Test comment");
        testComment.setTimestamp(System.currentTimeMillis());

        commentResponse = new CommentResponse(testComment, "Test Author", "/images/profile.jpg");

        likeResponse = new LikeResponse(1L, "user@example.com", "Test User", "/images/profile.jpg", System.currentTimeMillis());
    }

    @Test
    void getComments_Success() throws Exception {
        List<CommentResponse> comments = Arrays.asList(commentResponse);
        when(interactionService.getCommentsByPost(1L)).thenReturn(comments);

        mockMvc.perform(get("/api/interactions/posts/1/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("comment-1"))
                .andExpect(jsonPath("$[0].content").value("Test comment"))
                .andExpect(jsonPath("$[0].authorName").value("Test Author"));

        verify(interactionService).getCommentsByPost(1L);
    }

    @Test
    void addComment_Success() throws Exception {
        when(interactionService.addComment(any(Comment.class))).thenReturn(testComment);

        mockMvc.perform(post("/api/interactions/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testComment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("comment-1"))
                .andExpect(jsonPath("$.content").value("Test comment"));

        verify(interactionService).addComment(any(Comment.class));
    }

    @Test
    void deleteComment_Success() throws Exception {
        doNothing().when(interactionService).deleteComment("comment-1");

        mockMvc.perform(delete("/api/interactions/comments/comment-1"))
                .andExpect(status().isNoContent());

        verify(interactionService).deleteComment("comment-1");
    }

    @Test
    void getLikes_Success() throws Exception {
        List<LikeResponse> likes = Arrays.asList(likeResponse);
        when(interactionService.getLikesByPost(1L)).thenReturn(likes);

        mockMvc.perform(get("/api/interactions/posts/1/likes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId").value(1))
                .andExpect(jsonPath("$[0].userEmail").value("user@example.com"))
                .andExpect(jsonPath("$[0].userName").value("Test User"));

        verify(interactionService).getLikesByPost(1L);
    }

    @Test
    void toggleLike_AddLike() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("userEmail", "user@example.com");

        when(interactionService.toggleLike(1L, "user@example.com")).thenReturn(true);

        mockMvc.perform(post("/api/interactions/posts/1/likes/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLiked").value(true));

        verify(interactionService).toggleLike(1L, "user@example.com");
    }

    @Test
    void toggleLike_RemoveLike() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("userEmail", "user@example.com");

        when(interactionService.toggleLike(1L, "user@example.com")).thenReturn(false);

        mockMvc.perform(post("/api/interactions/posts/1/likes/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isLiked").value(false));

        verify(interactionService).toggleLike(1L, "user@example.com");
    }

    @Test
    void toggleLike_MissingEmail() throws Exception {
        Map<String, String> request = new HashMap<>();

        mockMvc.perform(post("/api/interactions/posts/1/likes/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(interactionService, never()).toggleLike(anyLong(), anyString());
    }

    @Test
    void toggleLike_BlankEmail() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("userEmail", "");

        mockMvc.perform(post("/api/interactions/posts/1/likes/toggle")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(interactionService, never()).toggleLike(anyLong(), anyString());
    }
}
