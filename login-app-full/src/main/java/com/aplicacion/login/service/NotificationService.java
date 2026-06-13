package com.aplicacion.login.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.aplicacion.login.dto.NotificationDto;

@Service
public class NotificationService {
  private final SimpMessagingTemplate broker;

  public NotificationService(SimpMessagingTemplate broker) {
    this.broker = broker;
  }

  public void notifyUser(String userName, NotificationDto notification) {
    // envía a la cola privada del usuario: /user/{userId}/queue/notifications
    broker.convertAndSendToUser(
    		userName.toString(),
      "/queue/notifications",
      notification
    );
  }

  public void broadcast(NotificationDto notification) {
    // envía a todos los suscritos a /topic/alerts
    broker.convertAndSend("/topic/alerts", notification);
  }
}
