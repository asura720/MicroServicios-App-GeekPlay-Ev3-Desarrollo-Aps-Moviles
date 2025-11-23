package com.geekplay.interactionservice.repository;

import com.geekplay.interactionservice.model.Like;
import com.geekplay.interactionservice.model.LikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
// Usamos LikeId como la clave compuesta
public interface LikeRepository extends JpaRepository<Like, LikeId> {

    // 1. Obtiene todos los likes para un post
    List<Like> findByPostId(Long postId);
    
    // 2. Busca un like específico por el usuario y el post (para el toggle)
    Optional<Like> findByPostIdAndUserEmail(Long postId, String userEmail);
    
    // 3. Eliminar un like por sus claves compuestas
    @Transactional
    void deleteByPostIdAndUserEmail(Long postId, String userEmail);
}