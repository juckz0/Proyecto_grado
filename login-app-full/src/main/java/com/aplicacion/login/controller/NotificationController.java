package com.aplicacion.login.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aplicacion.login.dto.NotificationDto;
import com.aplicacion.login.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  /**  
   * Broadcast a todos los suscritos a /topic/alerts  
   */
  @PostMapping("/broadcast")
  public void broadcast(@RequestBody NotificationDto dto) {
    notificationService.broadcast(dto);
  }

  /**  
   * Notifica a un usuario concreto (cola privada)  
   */
  @PostMapping("/user/{userName}")
  public void notifyUser(
      @PathVariable String userName,
      @RequestBody NotificationDto dto
  ) {
    notificationService.notifyUser(userName, dto);
  }
}
