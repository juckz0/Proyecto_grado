package com.aplicacion.login.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {
	@Id @Column(name="name", length=50)
	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "role_permission",
			joinColumns = @JoinColumn(
					name = "role_name"           // columna que apunta a roles.name
			
					),
			inverseJoinColumns = @JoinColumn(
					name = "permission_key",      // columna que ahora apunta a permission.permission_key
					referencedColumnName = "permission_key"
					)
			)
	private Set<Permission> permissions;

	// constructor a mano:
	public Role(String name) {
		this.name = name;
		this.permissions = new HashSet<>();
	}
}
