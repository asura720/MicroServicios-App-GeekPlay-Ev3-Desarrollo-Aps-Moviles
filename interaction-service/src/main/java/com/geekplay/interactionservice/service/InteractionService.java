package com.geekplay.interactionservice.service;

import com.geekplay.interactionservice.dto.CommentResponse; // ⬅️ NUEVO DTO ENRIQUECIDO
import com.geekplay.interactionservice.dto.UserClientResponse;
import com.geekplay.interactionservice.model.Comment;
import com.geekplay.interactionservice.model.Like;
import com.geekplay.interactionservice.repository.CommentRepository;
import com.geekplay.interactionservice.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InteractionService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired // ⬅️ Inyectar el nuevo cliente HTTP
    private UserServiceClient userServiceClient;

    // ----------------------------------------------------
    // Lógica de Comentarios (MODIFICADO PARA ENRIQUECIMIENTO)
    // ----------------------------------------------------

    /**
     * Obtiene los comentarios y ENRIQUECE cada uno con el nombre y foto del autor 
     * mediante una llamada segura al User Service (8083).
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPost(Long postId) {
        return commentRepository.findByPostIdOrderByTimestampDesc(postId).stream()
            .map(comment -> {
                // 1. Obtener detalles del autor del User Service de forma SEGURA
                Optional<UserClientResponse> userDetails = userServiceClient.getUserDetails(comment.getAuthorId());

                // 2. Asignar valores (si el usuario no se encuentra, usamos un valor por defecto)
                String authorName = userDetails.map(UserClientResponse::getName).orElse("Usuario Eliminado");
                String profileImage = userDetails.map(UserClientResponse::getProfileImagePath).orElse(null);

                // 3. Mapear al DTO enriquecido (con fecha legible)
                return new CommentResponse(comment, authorName, profileImage);
            })
            .toList();
    }
    
    @Transactional
    public Comment addComment(Comment newComment) {
        newComment.setId(UUID.randomUUID().toString());
        newComment.setTimestamp(System.currentTimeMillis());
        return commentRepository.save(newComment);
    }

    @Transactional
    public void deleteComment(String commentId) {
        commentRepository.deleteById(commentId);
    }
    
    // ----------------------------------------------------
    // Lógica de Likes (no necesita enriquecimiento)
    // ----------------------------------------------------

// En InteractionService.java
    
    // Cambia el tipo de retorno a List<LikeResponse>
    @Transactional(readOnly = true)
    public List<com.geekplay.interactionservice.dto.LikeResponse> getLikesByPost(Long postId) {
        return likeRepository.findByPostId(postId).stream()
            .map(like -> {
                // 1. Buscar datos del usuario por Email
                Optional<UserClientResponse> user = userServiceClient.getUserDetailsByEmail(like.getUserEmail());
                
                String name = user.map(UserClientResponse::getName).orElse("Usuario");
                String image = user.map(UserClientResponse::getProfileImagePath).orElse(null);

                // 2. Crear respuesta enriquecida
                return new com.geekplay.interactionservice.dto.LikeResponse(
                    like.getPostId(), 
                    like.getUserEmail(), 
                    name, 
                    image, 
                    like.getTimestamp()
                );
            })
            .toList();
    }
    
    @Transactional
    public boolean toggleLike(Long postId, String userEmail) {
        Optional<Like> existingLike = likeRepository.findByPostIdAndUserEmail(postId, userEmail);
        
        if (existingLike.isPresent()) {
            likeRepository.deleteByPostIdAndUserEmail(postId, userEmail);
            return false; 
        } else {
            Like newLike = new Like();
            newLike.setPostId(postId);
            newLike.setUserEmail(userEmail);
            newLike.setTimestamp(System.currentTimeMillis());
            likeRepository.save(newLike);
            return true; 
        }
    }
}