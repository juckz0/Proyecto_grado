package com.aplicacion.login.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permission")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Permission {
	/** Ahora el campo permissionKey es la clave primaria */
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "permission_key", length = 50)
	private String permissionKey;

	@Column(name = "url_pattern", length = 200, nullable = false)
	private String urlPattern;

	@Column(name = "http_method", length = 10, nullable = false)
	private String httpMethod;
}
