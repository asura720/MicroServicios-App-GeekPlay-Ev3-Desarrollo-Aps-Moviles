package com.geekplay.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email")) //  email debe ser único
@Data //  Genera Getters, Setters, toString, hashCode, equals (Lombok)
@NoArgsConstructor // Constructor sin argumentos
@AllArgsConstructor // Constructor con todos los argumentos
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Clave primaria autoincremental

    private String name;
    
    private String email;
    
    private String phone;
    
    // 🚨 Almacena el hash BCrypt (BCrypt genera un hash de más de 60 caracteres)
    @Column(length = 100) 
    private String password;
    
    private String profileImagePath;
    
    private boolean isAdmin = false; // Indica si es administrador
    
    private Long bannedUntil; // Timestamp hasta el que el usuario está baneado
}