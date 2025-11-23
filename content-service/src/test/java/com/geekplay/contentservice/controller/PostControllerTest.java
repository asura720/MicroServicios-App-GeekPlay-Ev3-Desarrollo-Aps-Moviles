package com.geekplay.contentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geekplay.contentservice.dto.PostResponse;
import com.geekplay.contentservice.model.Post;
import com.geekplay.contentservice.service.PostService;
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

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    private PostResponse postResponse;
    private Post post;

    @BeforeEach
    void setUp() {
        post = new Post();
        post.setId(1L);
        post.setTitle("Test Post");
        post.setContent("Test content");
        post.setSummary("Test summary");
        post.setCategory("VIDEOJUEGOS");
        post.setAuthorId(1L);
        post.setPublishedAt(System.currentTimeMillis());

        postResponse = new PostResponse(post, "Test Author", "/images/profile.jpg");
    }

    @Test
    void getAllPosts_Success() throws Exception {
        List<PostResponse> posts = Arrays.asList(postResponse);
        when(postService.findAll()).thenReturn(posts);

        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("Test Post"))
                .andExpect(jsonPath("$[0].authorName").value("Test Author"));

        verify(postService).findAll();
    }

    @Test
    void getPostsByCategory_Success() throws Exception {
        List<PostResponse> posts = Arrays.asList(postResponse);
        when(postService.findByCategory("VIDEOJUEGOS")).thenReturn(posts);

        mockMvc.perform(get("/api/posts/category/VIDEOJUEGOS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("VIDEOJUEGOS"));

        verify(postService).findByCategory("VIDEOJUEGOS");
    }

    @Test
    void getPostsByAuthor_Success() throws Exception {
        List<PostResponse> posts = Arrays.asList(postResponse);
        when(postService.findByAuthor(1L)).thenReturn(posts);

        mockMvc.perform(get("/api/posts/author/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].authorId").value(1));

        verify(postService).findByAuthor(1L);
    }

    @Test
    void searchPosts_Success() throws Exception {
        List<PostResponse> posts = Arrays.asList(postResponse);
        when(postService.searchPosts("Test")).thenReturn(posts);

        mockMvc.perform(get("/api/posts/search")
                .param("query", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Post"));

        verify(postService).searchPosts("Test");
    }

    @Test
    void getPostById_Success() throws Exception {
        when(postService.findById(1L)).thenReturn(postResponse);

        mockMvc.perform(get("/api/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Post"));

        verify(postService).findById(1L);
    }

    @Test
    void getPostById_NotFound() throws Exception {
        when(postService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/posts/999"))
                .andExpect(status().isNotFound());

        verify(postService).findById(999L);
    }

    @Test
    void createPost_Success() throws Exception {
        when(postService.createPost(any(Post.class))).thenReturn(postResponse);

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Test Post"));

        verify(postService).createPost(any(Post.class));
    }

    @Test
    void deletePost_Success() throws Exception {
        doNothing().when(postService).deletePost(1L);

        mockMvc.perform(delete("/api/posts/1"))
                .andExpect(status().isNoContent());

        verify(postService).deletePost(1L);
    }
}
