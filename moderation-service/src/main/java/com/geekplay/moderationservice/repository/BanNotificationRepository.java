package com.geekplay.moderationservice.repository;

import com.geekplay.moderationservice.model.BanNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BanNotificationRepository extends JpaRepository<BanNotification, Long> {
    
    // Obtiene notificaciones por ID de usuario, las más nuevas primero
    List<BanNotification> findByUserIdOrderByTimestampDesc(Long userId);
}