package com.aplicacion.login.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Habilita un broker simple en memoria en prefijos /topic y /queue
    registry.enableSimpleBroker("/topic", "/queue");
    // Prefijo para mensajes entrantes desde el cliente
    registry.setApplicationDestinationPrefixes("/app");
    // Prefijo para destinos “privados” de usuario
    registry.setUserDestinationPrefix("/user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Expón un endpoint en /ws con soporte SockJS
    registry
      .addEndpoint("/ws")
      .setAllowedOriginPatterns("*")  // en prod pon dominio concreto
      .withSockJS();
  }
}
