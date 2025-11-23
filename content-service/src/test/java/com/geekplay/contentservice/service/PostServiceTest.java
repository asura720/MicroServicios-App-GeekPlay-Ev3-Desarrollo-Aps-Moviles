package com.geekplay.contentservice.service;

import com.geekplay.contentservice.dto.PostResponse;
import com.geekplay.contentservice.dto.UserClientResponse;
import com.geekplay.contentservice.model.Post;
import com.geekplay.contentservice.repository.PostRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private PostService postService;

    private Post testPost;
    private UserClientResponse userClientResponse;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setId(1L);
        testPost.setTitle("Test Post");
        testPost.setContent("Test content");
        testPost.setSummary("Test summary");
        testPost.setCategory("VIDEOJUEGOS");
        testPost.setAuthorId(1L);
        testPost.setPublishedAt(System.currentTimeMillis());

        userClientResponse = new UserClientResponse();
        userClientResponse.setId(1L);
        userClientResponse.setName("Test Author");
        userClientResponse.setProfileImagePath("/images/profile.jpg");
    }

    @Test
    void findAll_Success() {
        when(postRepository.findAllByOrderByPublishedAtDesc()).thenReturn(Arrays.asList(testPost));
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.of(userClientResponse));

        List<PostResponse> result = postService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Post", result.get(0).getTitle());
        assertEquals("Test Author", result.get(0).getAuthorName());
        verify(postRepository).findAllByOrderByPublishedAtDesc();
        verify(userServiceClient).getUserDetails(1L);
    }

    @Test
    void findAll_WithUnknownUser() {
        when(postRepository.findAllByOrderByPublishedAtDesc()).thenReturn(Arrays.asList(testPost));
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.empty());

        List<PostResponse> result = postService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Usuario Desconocido", result.get(0).getAuthorName());
    }

    @Test
    void findById_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.of(userClientResponse));

        PostResponse result = postService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Post", result.getTitle());
        assertEquals("Test Author", result.getAuthorName());
        verify(postRepository).findById(1L);
    }

    @Test
    void findById_NotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());

        PostResponse result = postService.findById(999L);

        assertNull(result);
        verify(userServiceClient, never()).getUserDetails(anyLong());
    }

    @Test
    void findByCategory_Success() {
        when(postRepository.findByCategoryOrderByPublishedAtDesc("VIDEOJUEGOS"))
                .thenReturn(Arrays.asList(testPost));
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.of(userClientResponse));

        List<PostResponse> result = postService.findByCategory("VIDEOJUEGOS");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("VIDEOJUEGOS", result.get(0).getCategory());
        verify(postRepository).findByCategoryOrderByPublishedAtDesc("VIDEOJUEGOS");
    }

    @Test
    void findByAuthor_Success() {
        when(postRepository.findByAuthorIdOrderByPublishedAtDesc(1L))
                .thenReturn(Arrays.asList(testPost));
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.of(userClientResponse));

        List<PostResponse> result = postService.findByAuthor(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getAuthorId());
        verify(postRepository).findByAuthorIdOrderByPublishedAtDesc(1L);
    }

    @Test
    void searchPosts_Success() {
        when(postRepository.searchPosts("test")).thenReturn(Arrays.asList(testPost));
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.of(userClientResponse));

        List<PostResponse> result = postService.searchPosts("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository).searchPosts("test");
    }

    @Test
    void createPost_Success() {
        Post newPost = new Post();
        newPost.setTitle("New Post");
        newPost.setContent("New content");
        newPost.setAuthorId(1L);

        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post saved = invocation.getArgument(0);
            saved.setId(2L);
            return saved;
        });
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.of(userClientResponse));

        PostResponse result = postService.createPost(newPost);

        assertNotNull(result);
        assertEquals("New Post", result.getTitle());
        assertNotNull(newPost.getPublishedAt());
        verify(postRepository).save(newPost);
    }

    @Test
    void createPost_SetsPublishedAt() {
        Post newPost = new Post();
        newPost.setTitle("New Post");
        newPost.setAuthorId(1L);

        when(postRepository.save(any(Post.class))).thenReturn(newPost);
        when(userServiceClient.getUserDetails(1L)).thenReturn(Optional.of(userClientResponse));

        long beforeTime = System.currentTimeMillis();
        postService.createPost(newPost);
        long afterTime = System.currentTimeMillis();

        assertNotNull(newPost.getPublishedAt());
        assertTrue(newPost.getPublishedAt() >= beforeTime);
        assertTrue(newPost.getPublishedAt() <= afterTime);
    }

    @Test
    void deletePost_Success() {
        doNothing().when(postRepository).deleteById(1L);

        postService.deletePost(1L);

        verify(postRepository).deleteById(1L);
    }
}
