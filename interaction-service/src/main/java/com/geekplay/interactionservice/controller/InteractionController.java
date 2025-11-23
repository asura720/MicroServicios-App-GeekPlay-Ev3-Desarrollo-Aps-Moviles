package com.geekplay.interactionservice.controller;

import com.geekplay.interactionservice.dto.CommentResponse;
import com.geekplay.interactionservice.dto.LikeResponse;
import com.geekplay.interactionservice.model.Comment;
import com.geekplay.interactionservice.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/interactions")
@Tag(name = "Interacciones", description = "API para gestión de comentarios y likes")
public class InteractionController {

    @Autowired
    private InteractionService interactionService;

    // ----------------------------------------------------
    // Endpoints de Comentarios
    // ----------------------------------------------------

    @GetMapping("/posts/{postId}/comments")
    @Operation(summary = "Listar comentarios", description = "Obtiene la lista de comentarios de un post, enriquecidos con datos del autor (nombre y foto).")
    @ApiResponse(responseCode = "200", description = "Comentarios recuperados exitosamente")
    public List<CommentResponse> getComments(@PathVariable Long postId) {
        return interactionService.getCommentsByPost(postId);
    }

    @PostMapping("/comments")
    @Operation(summary = "Agregar comentario", description = "Guarda un nuevo comentario en una publicación.")
    @ApiResponse(responseCode = "201", description = "Comentario creado exitosamente")
    public ResponseEntity<Comment> addComment(@RequestBody Comment newComment) {
        Comment added = interactionService.addComment(newComment);
        return new ResponseEntity<>(added, HttpStatus.CREATED);
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "Eliminar comentario", description = "Borra permanentemente un comentario por su ID.")
    @ApiResponse(responseCode = "204", description = "Comentario eliminado correctamente")
    public ResponseEntity<Void> deleteComment(@PathVariable String commentId) {
        interactionService.deleteComment(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // ----------------------------------------------------
    // Endpoints de Likes
    // ----------------------------------------------------

    @GetMapping("/posts/{postId}/likes")
    @Operation(summary = "Listar likes", description = "Obtiene la lista de usuarios que han dado 'Me gusta' a un post.")
    @ApiResponse(responseCode = "200", description = "Likes recuperados exitosamente")
    public List<LikeResponse> getLikes(@PathVariable Long postId) {
        return interactionService.getLikesByPost(postId);
    }

    @PostMapping("/posts/{postId}/likes/toggle")
    @Operation(summary = "Alternar Like (Toggle)", description = "Si el usuario ya dio like, lo quita. Si no, lo agrega.")
    @ApiResponse(responseCode = "200", description = "Estado del like actualizado (true=agregado, false=quitado)")
    @ApiResponse(responseCode = "400", description = "Falta el email del usuario en la petición")
    public ResponseEntity<Map<String, Boolean>> toggleLike(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        String userEmail = request.get("userEmail");
        if (userEmail == null || userEmail.isBlank()) {
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        boolean isLikedNow = interactionService.toggleLike(postId, userEmail);
        return ResponseEntity.ok(Map.of("isLiked", isLikedNow));
    }
}