package com.geekplay.interactionservice.repository;

import com.geekplay.interactionservice.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// String es el ID del Comment (UUID)
public interface CommentRepository extends JpaRepository<Comment, String> {

    // Obtiene todos los comentarios para un post, ordenados por fecha descendente
    List<Comment> findByPostIdOrderByTimestampDesc(Long postId); 
    

}