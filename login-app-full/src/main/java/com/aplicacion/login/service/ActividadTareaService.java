package com.aplicacion.login.service;

import com.aplicacion.login.dto.ActividadTareaDto;
import com.aplicacion.login.entity.ActividadTarea;
import com.aplicacion.login.entity.ActividadTareaId;
import com.aplicacion.login.entity.Role;
import com.aplicacion.login.entity.Tarea;
import com.aplicacion.login.entity.User;
import com.aplicacion.login.exception.ApiException;
import com.aplicacion.login.repository.ActividadTareaRepository;
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
public class ActividadTareaService {
	private final ActividadTareaRepository actividadRepository;
	private final TareaRepository tareaRepository;
	private final UserRepository userRepository;
	private final AuthService authService;

	public List<ActividadTareaDto> getActividades(String tareaId, String token) {
		User user = getUsuarioActual(token);
		Tarea tarea = getTarea(tareaId);
		validarAccesoTarea(user, tarea);

		List<ActividadTarea> actividades = esAdmin(user)
				? actividadRepository.findById_TareaId(tareaId)
				: actividadRepository.findById_TareaIdAndId_UserId(tareaId, user.getId());

		return actividades.stream()
				.map(this::toDto)
				.toList();
	}

	@Transactional
	public ActividadTareaDto registrarActividad(String tareaId, ActividadTareaDto request, String token) {
		User user = getUsuarioActual(token);
		Tarea tarea = getTarea(tareaId);
		validarAccesoTarea(user, tarea);

		if (request.getActividad_desc() == null || request.getActividad_desc().isBlank()) {
			throw new ApiException(
					"ACTIVIDAD_DESC_REQUIRED",
					"La descripcion de la actividad es obligatoria",
					HttpStatus.BAD_REQUEST
					);
		}

		if (request.getTiempo_reg() == null || request.getTiempo_reg() <= 0) {
			throw new ApiException(
					"TIEMPO_REG_INVALID",
					"El tiempo registrado debe ser mayor a cero",
					HttpStatus.BAD_REQUEST
					);
		}

		Date fechaRegistro = parseDate(request.getFecha_registro(), "fecha_registro");

		ActividadTareaId id = new ActividadTareaId(user.getId(), tarea.getTarea_id(), request.getActividad_desc());

		if (actividadRepository.existsById(id)) {
			throw new ApiException(
					"ACTIVIDAD_ALREADY_EXISTS",
					"Ya existe una actividad con esa descripcion para esta tarea",
					HttpStatus.CONFLICT
					);
		}

		ActividadTarea actividad = ActividadTarea.builder()
				.id(id)
				.tiempoReg(request.getTiempo_reg())
				.fechaRegistro(fechaRegistro)
				.build();

		return toDto(actividadRepository.save(actividad));
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

	private ActividadTareaDto toDto(ActividadTarea actividad) {
		return new ActividadTareaDto(
				actividad.getId().getUserId(),
				actividad.getId().getTareaId(),
				actividad.getId().getActividadDesc(),
				actividad.getTiempoReg(),
				actividad.getFechaRegistro() != null ? actividad.getFechaRegistro().toString() : null
				);
	}

	private Date parseDate(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			return new Date(System.currentTimeMillis());
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
