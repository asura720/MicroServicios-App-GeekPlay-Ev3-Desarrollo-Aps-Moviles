package com.geekplay.contentservice.repository;

import com.geekplay.contentservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// ✅ CORREGIDO: El JpaRepository ahora usa Long para la clave primaria (ID)
public interface PostRepository extends JpaRepository<Post, Long> { 

    // Obtiene todos los posts ordenados por la fecha de publicación
    List<Post> findAllByOrderByPublishedAtDesc();

    // 1. Filtrar por Categoría
    List<Post> findByCategoryOrderByPublishedAtDesc(String category);

    // 2. Buscar Posts
    @Query("SELECT p FROM Post p WHERE p.title LIKE %:query% OR p.summary LIKE %:query% OR p.content LIKE %:query% ORDER BY p.publishedAt DESC")
    List<Post> searchPosts(String query);
    
    // 3. Filtrar por Autor (usando el Long authorId)
    List<Post> findByAuthorIdOrderByPublishedAtDesc(Long authorId);
}