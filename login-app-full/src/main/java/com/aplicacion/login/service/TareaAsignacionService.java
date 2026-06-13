package com.aplicacion.login.service;

import com.aplicacion.login.dto.TareaUsuarioDto;
import com.aplicacion.login.entity.Role;
import com.aplicacion.login.entity.Tarea;
import com.aplicacion.login.entity.User;
import com.aplicacion.login.exception.ApiException;
import com.aplicacion.login.repository.TareaRepository;
import com.aplicacion.login.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TareaAsignacionService {
	private final UserRepository userRepository;
	private final TareaRepository tareaRepository;
	private final AuthService authService;

	@Transactional
	public List<TareaUsuarioDto> getUsuariosAsignados(String tareaId, String token) {
		User usuarioActual = getUsuarioActual(token);
		Tarea tarea = getTarea(tareaId);
		validarAccesoTarea(usuarioActual, tarea);

		return userRepository.findAll().stream()
				.filter(user -> user.getTareas().stream()
						.anyMatch(ttarea -> ttarea.getTarea_id().equals(tareaId)))
				.map(this::toDto)
				.toList();
	}

	@Transactional
	public List<TareaUsuarioDto> asignarUsuario(String tareaId, Long userId, String token) {
		User usuarioActual = getUsuarioActual(token);
		validarAdmin(usuarioActual);
		Tarea tarea = getTarea(tareaId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ApiException(
						"USER_NOT_FOUND",
						"El usuario '" + userId + "' no existe",
						HttpStatus.NOT_FOUND
						));

		user.getTareas().add(tarea);
		userRepository.save(user);

		return getUsuariosAsignados(tareaId, token);
	}

	@Transactional
	public List<TareaUsuarioDto> quitarUsuario(String tareaId, Long userId, String token) {
		User usuarioActual = getUsuarioActual(token);
		validarAdmin(usuarioActual);
		getTarea(tareaId);

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ApiException(
						"USER_NOT_FOUND",
						"El usuario '" + userId + "' no existe",
						HttpStatus.NOT_FOUND
						));

		user.getTareas().removeIf(tarea -> tarea.getTarea_id().equals(tareaId));
		userRepository.save(user);

		return getUsuariosAsignados(tareaId, token);
	}

	private Tarea getTarea(String tareaId) {
		return tareaRepository.findById(tareaId)
				.orElseThrow(() -> new ApiException(
						"TAREA_NOT_FOUND",
						"La tarea '" + tareaId + "' no existe",
						HttpStatus.NOT_FOUND
						));
	}

	private User getUsuarioActual(String token) {
		if (token == null || !authService.validateToken(token)) {
			throw new ApiException(
					"UNAUTHORIZED",
					"No estas autenticado",
					HttpStatus.UNAUTHORIZED
					);
		}

		String username = authService.getUsernameFromToken(token);
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(
						"USER_NOT_FOUND",
						"Usuario no encontrado",
						HttpStatus.NOT_FOUND
						));
	}

	private boolean esAdmin(User user) {
		return user.getRoles().stream()
				.map(Role::getName)
				.anyMatch("ADMIN"::equals);
	}

	private void validarAdmin(User user) {
		if (!esAdmin(user)) {
			throw new ApiException(
					"ACCESS_DENIED",
					"Solo un administrador puede asignar tareas a otros usuarios",
					HttpStatus.FORBIDDEN
					);
		}
	}

	private void validarAccesoTarea(User user, Tarea tarea) {
		if (esAdmin(user)) {
			return;
		}

		boolean asignada = user.getTareas().stream()
				.anyMatch(userTarea -> userTarea.getTarea_id().equals(tarea.getTarea_id()));

		if (!asignada) {
			throw new ApiException(
					"ACCESS_DENIED",
					"No tienes permiso para esta tarea",
					HttpStatus.FORBIDDEN
					);
		}
	}

	private TareaUsuarioDto toDto(User user) {
		return new TareaUsuarioDto(
				user.getId(),
				user.getUsername(),
				user.getCorreo()
				);
	}
}
