package com.aplicacion.login.service;

import com.aplicacion.login.dto.TareaDto;
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

import java.sql.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TareaService {
	private final TareaRepository tareaRepository;
	private final UserRepository userRepository;
	private final AuthService authService;

	public List<TareaDto> getAll(String token) {
		User user = getUsuarioActual(token);

		if (esAdmin(user)) {
			return tareaRepository.findAll().stream()
					.map(this::toDto)
					.toList();
		}

		return user.getTareas().stream()
				.map(this::toDto)
				.toList();
	}

	@Transactional
	public TareaDto crearTarea(TareaDto request, String token) {
		return insertarTarea(request, token);
	}

	@Transactional
	public TareaDto insertarTarea(TareaDto request, String token) {
		User user = getUsuarioActual(token);
		String tareaId = generarSiguienteTareaId();

		if (tareaRepository.existsById(tareaId)) {
			throw new ApiException(
					"TAREA_ALREADY_EXISTS",
					"La tarea '" + tareaId + "' ya existe",
					HttpStatus.CONFLICT
					);
		}

		Tarea tarea = Tarea.builder()
				.tarea_id(tareaId)
				.tarea_des(request.getTarea_des())
				.fecha_ini(parseDate(request.getFecha_ini(), "fecha_ini"))
				.fecha_fin(parseDate(request.getFecha_fin(), "fecha_fin"))
				.estado(request.getEstado())
				.build();

		Tarea tareaGuardada = tareaRepository.save(tarea);

		if (!esAdmin(user)) {
			user.getTareas().add(tareaGuardada);
			userRepository.save(user);
		}

		return toDto(tareaGuardada);
	}

	public TareaDto actualizarTarea(String tareaId, TareaDto request, String token) {
		User user = getUsuarioActual(token);
		validarId(tareaId);

		Tarea tarea = tareaRepository.findById(tareaId)
				.orElseThrow(() -> new ApiException(
						"TAREA_NOT_FOUND",
						"La tarea '" + tareaId + "' no existe",
						HttpStatus.NOT_FOUND
						));
		validarAccesoTarea(user, tarea);

		tarea.setTarea_des(request.getTarea_des());
		tarea.setFecha_ini(parseDate(request.getFecha_ini(), "fecha_ini"));
		tarea.setFecha_fin(parseDate(request.getFecha_fin(), "fecha_fin"));
		tarea.setEstado(request.getEstado());

		return toDto(tareaRepository.save(tarea));
	}

	@Transactional
	public void borrarTarea(String tareaId, String token) {
		User user = getUsuarioActual(token);
		validarId(tareaId);

		Tarea tarea = tareaRepository.findById(tareaId)
				.orElseThrow(() -> new ApiException(
						"TAREA_NOT_FOUND",
						"La tarea '" + tareaId + "' no existe",
						HttpStatus.NOT_FOUND
						));
		validarAccesoTarea(user, tarea);

		userRepository.findAll().forEach(usuario -> {
			if (usuario.getTareas().removeIf(userTarea -> userTarea.getTarea_id().equals(tareaId))) {
				userRepository.save(usuario);
			}
		});
		tareaRepository.deleteById(tareaId);
	}

	private TareaDto toDto(Tarea tarea) {
		return new TareaDto(
				tarea.getTarea_id(),
				tarea.getTarea_des(),
				tarea.getFecha_ini() != null ? tarea.getFecha_ini().toString() : null,
				tarea.getFecha_fin() != null ? tarea.getFecha_fin().toString() : null,
				tarea.getEstado()
				);
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

	private void validarId(String tareaId) {
		if (tareaId == null || tareaId.isBlank()) {
			throw new ApiException(
					"TAREA_ID_REQUIRED",
					"El id de la tarea es obligatorio",
					HttpStatus.BAD_REQUEST
					);
		}
	}

	private synchronized String generarSiguienteTareaId() {
		long maxId = tareaRepository.findAll().stream()
				.map(Tarea::getTarea_id)
				.map(this::parseLongOrNull)
				.filter(id -> id != null)
				.mapToLong(Long::longValue)
				.max()
				.orElse(0L);

		return String.valueOf(maxId + 1);
	}

	private Long parseLongOrNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return Long.valueOf(value);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private Date parseDate(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			return null;
		}

		try {
			return Date.valueOf(value);
		} catch (IllegalArgumentException ex) {
			throw new ApiException(
					"INVALID_DATE",
					"El campo " + fieldName + " debe tener formato yyyy-MM-dd",
					HttpStatus.BAD_REQUEST
					);
		}
	}
}
