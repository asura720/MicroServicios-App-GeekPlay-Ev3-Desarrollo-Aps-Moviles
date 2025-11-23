package com.geekplay.interactionservice.service;

import com.geekplay.interactionservice.dto.CommentResponse;
import com.geekplay.interactionservice.dto.LikeResponse;
import com.geekplay.interactionservice.dto.UserClientResponse;
import com.geekplay.interactionservice.model.Comment;
import com.geekplay.interactionservice.model.Like;
import com.geekplay.interactionservice.repository.CommentRepository;
import com.geekplay.interactionservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InteractionServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private InteractionService interactionService;

    private Comment testComment;
    private Like testLike;
    private UserClientResponse userClientResponse;

    @BeforeEach
    void setUp() {
        testComment = new Comment();
        testComment.setId("comment-1");
        testComment.setPostId(1L);
        testComment.setAuthorId(1L);
        testComment.setContent("Test comment");
        testComment.setTimestamp(System.currentTimeMillis());

        testLike = new Like();
        testLike.setPostId(1L);
        testLike.setUserEmail("user@example.com");
        testLike.setTimestamp(System.currentTimeMillis());

        userClientResponse = new UserClientResponse();
        userClientResponse.setId(1L);
        userClientResponse.setName("Test User");
        userClientResponse.setProfileImagePath("/images/profile.jpg");
    }

    @Test
    void getCommentsByPost_Success() {
        when(commentRepository.findByPostIdOrderByTimestampDesc(1L))
                .thenReturn(Arrays.asList(testComment));
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.of(userClientResponse));

        List<CommentResponse> result = interactionService.getCommentsByPost(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("comment-1", result.get(0).getId());
        assertEquals("Test comment", result.get(0).getContent());
        assertEquals("Test User", result.get(0).getAuthorName());
        assertEquals("/images/profile.jpg", result.get(0).getAuthorProfileImageUrl());
        verify(commentRepository).findByPostIdOrderByTimestampDesc(1L);
        verify(userServiceClient).getUserDetails(1L);
    }

    @Test
    void getCommentsByPost_UserNotFound() {
        when(commentRepository.findByPostIdOrderByTimestampDesc(1L))
                .thenReturn(Arrays.asList(testComment));
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.empty());

        List<CommentResponse> result = interactionService.getCommentsByPost(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Usuario Eliminado", result.get(0).getAuthorName());
        assertNull(result.get(0).getAuthorProfileImageUrl());
    }

    @Test
    void addComment_Success() {
        Comment newComment = new Comment();
        newComment.setPostId(1L);
        newComment.setAuthorId(1L);
        newComment.setContent("New comment");

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment saved = invocation.getArgument(0);
            return saved;
        });

        Comment result = interactionService.addComment(newComment);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getTimestamp());
        assertEquals("New comment", result.getContent());
        verify(commentRepository).save(newComment);
    }

    @Test
    void addComment_SetsIdAndTimestamp() {
        Comment newComment = new Comment();
        newComment.setPostId(1L);
        newComment.setAuthorId(1L);
        newComment.setContent("New comment");

        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);

        long beforeTime = System.currentTimeMillis();
        Comment result = interactionService.addComment(newComment);
        long afterTime = System.currentTimeMillis();

        assertNotNull(result.getId());
        assertNotNull(result.getTimestamp());
        assertTrue(result.getTimestamp() >= beforeTime);
        assertTrue(result.getTimestamp() <= afterTime);
    }

    @Test
    void deleteComment_Success() {
        doNothing().when(commentRepository).deleteById("comment-1");

        interactionService.deleteComment("comment-1");

        verify(commentRepository).deleteById("comment-1");
    }

    @Test
    void getLikesByPost_Success() {
        when(likeRepository.findByPostId(1L)).thenReturn(Arrays.asList(testLike));
        when(userServiceClient.getUserDetailsByEmail("user@example.com"))
                .thenReturn(Optional.of(userClientResponse));

        List<LikeResponse> result = interactionService.getLikesByPost(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getPostId());
        assertEquals("user@example.com", result.get(0).getUserEmail());
        assertEquals("Test User", result.get(0).getUserName());
        assertEquals("/images/profile.jpg", result.get(0).getUserProfileImageUrl());
        verify(likeRepository).findByPostId(1L);
        verify(userServiceClient).getUserDetailsByEmail("user@example.com");
    }

    @Test
    void getLikesByPost_UserNotFound() {
        when(likeRepository.findByPostId(1L)).thenReturn(Arrays.asList(testLike));
        when(userServiceClient.getUserDetailsByEmail("user@example.com"))
                .thenReturn(Optional.empty());

        List<LikeResponse> result = interactionService.getLikesByPost(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Usuario", result.get(0).getUserName());
        assertNull(result.get(0).getUserProfileImageUrl());
    }

    @Test
    void toggleLike_AddLike() {
        when(likeRepository.findByPostIdAndUserEmail(1L, "user@example.com"))
                .thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = interactionService.toggleLike(1L, "user@example.com");

        assertTrue(result);
        verify(likeRepository).findByPostIdAndUserEmail(1L, "user@example.com");
        verify(likeRepository).save(any(Like.class));
        verify(likeRepository, never()).deleteByPostIdAndUserEmail(anyLong(), anyString());
    }

    @Test
    void toggleLike_RemoveLike() {
        when(likeRepository.findByPostIdAndUserEmail(1L, "user@example.com"))
                .thenReturn(Optional.of(testLike));
        doNothing().when(likeRepository).deleteByPostIdAndUserEmail(1L, "user@example.com");

        boolean result = interactionService.toggleLike(1L, "user@example.com");

        assertFalse(result);
        verify(likeRepository).findByPostIdAndUserEmail(1L, "user@example.com");
        verify(likeRepository).deleteByPostIdAndUserEmail(1L, "user@example.com");
        verify(likeRepository, never()).save(any(Like.class));
    }

    @Test
    void toggleLike_SetsTimestamp() {
        when(likeRepository.findByPostIdAndUserEmail(1L, "new@example.com"))
                .thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenAnswer(invocation -> {
            Like saved = invocation.getArgument(0);
            assertNotNull(saved.getTimestamp());
            assertEquals(1L, saved.getPostId());
            assertEquals("new@example.com", saved.getUserEmail());
            return saved;
        });

        interactionService.toggleLike(1L, "new@example.com");

        verify(likeRepository).save(any(Like.class));
    }
}
