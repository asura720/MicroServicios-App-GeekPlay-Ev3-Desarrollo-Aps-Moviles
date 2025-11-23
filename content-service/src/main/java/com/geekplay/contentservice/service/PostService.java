package com.geekplay.contentservice.service;

import com.geekplay.contentservice.dto.PostResponse;
import com.geekplay.contentservice.dto.UserClientResponse;
import com.geekplay.contentservice.model.Post;
import com.geekplay.contentservice.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserServiceClient userServiceClient; // ⬅️ Inyectamos el cliente

    // Método auxiliar para convertir Post -> PostResponse enriquecido
    private PostResponse mapToResponse(Post post) {
        // 1. Llamar al User Service
        Optional<UserClientResponse> user = userServiceClient.getUserDetails(post.getAuthorId());
        
        // 2. Extraer datos (o usar defaults)
        String name = user.map(UserClientResponse::getName).orElse("Usuario Desconocido");
        String image = user.map(UserClientResponse::getProfileImagePath).orElse(null);
        
        // 3. Crear DTO
        return new PostResponse(post, name, image);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll() {
        return postRepository.findAllByOrderByPublishedAtDesc().stream()
            .map(this::mapToResponse) // ✅ Usamos el mapeo enriquecido
            .toList();
    }

    @Transactional(readOnly = true)
    public PostResponse findById(Long id) {
        return postRepository.findById(id)
            .map(this::mapToResponse)
            .orElse(null);
    }
    
    @Transactional(readOnly = true)
    public List<PostResponse> findByCategory(String category) {
        return postRepository.findByCategoryOrderByPublishedAtDesc(category).stream()
            .map(this::mapToResponse)
            .toList();
    }
    
    @Transactional(readOnly = true)
    public List<PostResponse> findByAuthor(Long authorId) {
        return postRepository.findByAuthorIdOrderByPublishedAtDesc(authorId).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PostResponse> searchPosts(String query) {
        return postRepository.searchPosts(query).stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional
    public PostResponse createPost(Post newPost) {
        newPost.setPublishedAt(System.currentTimeMillis());
        Post savedPost = postRepository.save(newPost);
        return mapToResponse(savedPost);
    }
    
    @Transactional
    public void deletePost(Long postId) { 
        postRepository.deleteById(postId);
    }
}