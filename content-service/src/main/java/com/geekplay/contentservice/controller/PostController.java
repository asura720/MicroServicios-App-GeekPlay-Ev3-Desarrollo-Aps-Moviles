package com.geekplay.contentservice.controller;

import com.geekplay.contentservice.dto.PostResponse;
import com.geekplay.contentservice.model.Post;
import com.geekplay.contentservice.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "Publicaciones", description = "API para la gestión de contenido (crear, buscar y listar posts)")
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping
    @Operation(summary = "Listar todos los posts", description = "Obtiene la lista completa de publicaciones ordenadas por fecha descendente, con datos de autor enriquecidos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista recuperada exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public List<PostResponse> getAllPosts() {
        return postService.findAll(); 
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Filtrar por categoría", description = "Devuelve los posts que pertenecen a una categoría específica (VIDEOJUEGOS, PELICULAS, etc).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts filtrados recuperados"),
        @ApiResponse(responseCode = "400", description = "Categoría inválida")
    })
    public List<PostResponse> getPostsByCategory(@PathVariable String category) {
        return postService.findByCategory(category);
    }
    
    @GetMapping("/author/{authorId}")
    @Operation(summary = "Filtrar por autor", description = "Devuelve todas las publicaciones creadas por un usuario específico.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Posts del autor recuperados")
    })
    public List<PostResponse> getPostsByAuthor(@PathVariable Long authorId) {
        return postService.findByAuthor(authorId);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar posts", description = "Realiza una búsqueda de texto en el título, resumen o contenido del post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resultados de la búsqueda")
    })
    public List<PostResponse> searchPosts(@RequestParam String query) {
        return postService.searchPosts(query);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de un post", description = "Busca un post por su ID único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Post encontrado"),
        @ApiResponse(responseCode = "404", description = "Post no encontrado")
    })
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) { 
        PostResponse post = postService.findById(id);
        if (post != null) {
            return ResponseEntity.ok(post);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo post", description = "Guarda una nueva publicación en la base de datos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Post creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos en la petición"),
        @ApiResponse(responseCode = "500", description = "Error al guardar el post")
    })
    public ResponseEntity<PostResponse> createPost(@RequestBody Post newPost) {
        PostResponse created = postService.createPost(newPost);
        return new ResponseEntity<>(created, HttpStatus.CREATED); 
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un post", description = "Borra permanentemente una publicación por su ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Post eliminado correctamente (sin contenido)"),
        @ApiResponse(responseCode = "404", description = "El post no existe"),
        @ApiResponse(responseCode = "500", description = "Error interno al eliminar")
    })
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}