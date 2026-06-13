package com.aplicacion.login.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ui_routes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UIRoute {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String path;          // p.ej. "users"

  @Column(nullable = false)
  private String label;         // p.ej. "Usuarios"

  private String icon;          // p.ej. "group"

  @Column(name = "permission_key", nullable = false)
  private String permissionKey; // p.ej. "USER_VIEW"
}