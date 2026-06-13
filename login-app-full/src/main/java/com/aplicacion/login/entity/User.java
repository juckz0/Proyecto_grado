package com.aplicacion.login.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {
	private static final long serialVersionUID = 1L;
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;
	private String password;
	private String totpSecret;
    private String correo;
    private String primerNombre;
    private String segundoNombre;
    private String primerApellido;
    private String segundoApellido;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_roles",
			joinColumns        = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "role_name")
			)
	private Set<Role> roles = new HashSet<>();
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_tarea",
			joinColumns        = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "tarea_id")
			)
	private Set<Tarea> tareas = new HashSet<>();


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> auths = new HashSet<>();

		// 1) Añade tus roles (para @hasRole, etc)
		for (Role r : roles) {
			auths.add(new SimpleGrantedAuthority("ROLE_" + r.getName()));
		}

		// 2) Añade también cada permiso como autoridad “plana”
		//    (debe coincidir con los permissionKey en tu DB)
		for (Role r : roles) {
			for (Permission p : r.getPermissions()) {
				auths.add(new SimpleGrantedAuthority(p.getPermissionKey()));
			}
		}

		return auths;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}