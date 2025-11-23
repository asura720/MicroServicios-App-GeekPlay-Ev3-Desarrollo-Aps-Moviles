package com.geekplay.userservice.repository;

import com.geekplay.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
// Long es el tipo de la clave primaria (id) de la entidad User
public interface UserRepository extends JpaRepository<User, Long> {

    // Necesario para login y registro (replicando findByEmail de UserDao.kt)
    Optional<User> findByEmail(String email);
    
    // Función para el baneo (usada por el Moderation Service)
    // Replicando updateBannedUntil de UserDao.kt
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.bannedUntil = :bannedUntil WHERE u.id = :userId")
    void updateBannedUntil(Long userId, Long bannedUntil);
}