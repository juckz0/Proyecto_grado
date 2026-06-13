package com.aplicacion.login.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aplicacion.login.dto.UpdateUserRequest;
import com.aplicacion.login.dto.UserDto;
import com.aplicacion.login.entity.Role;
import com.aplicacion.login.repository.RoleRepository;
import com.aplicacion.login.repository.UserRepository;
import com.aplicacion.login.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
	private final UserRepository userRepository;
	private final RoleRepository roleRepo;
	private final UserService userService;

	/**
	 * Borra el usuario con ID dado.
	 * Requiere permiso USER_DELETE en tu UrlPermissionService/AuthorizationManager.
	 */
	@DeleteMapping("/users/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}
	@GetMapping("/users")
	public List<UserDto> getAll() {
		return userRepository.findAll().stream()
				.map(u -> new UserDto(u.getId(), u.getUsername(),u.getCorreo(), u.getPrimerNombre(),
						u.getSegundoNombre(),u.getPrimerApellido(),u.getSegundoApellido(),u.getRoles()))
				.toList();
	}
	@PutMapping("/users/{id}")
	public ResponseEntity<?> updateUser(
			@PathVariable Long id,
			@RequestBody UpdateUserRequest req
			) {
		return userRepository.findById(id)
				.map(user -> {
					if (req.getUsername() != null) {
						user.setUsername(req.getUsername());
						user.setCorreo(req.getCorreo());
					}
					if (req.getRoles() != null) {
						Set<Role> roles = req.getRoles().stream()
								.map(name -> roleRepo.findById(name)
										.orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + name)))
								.collect(Collectors.toSet());
						user.setRoles(roles);
					}
					userRepository.save(user);
					return ResponseEntity.ok().build();
				})
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
}