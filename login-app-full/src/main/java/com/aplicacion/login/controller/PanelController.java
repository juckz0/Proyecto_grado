package com.aplicacion.login.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aplicacion.login.dto.ActividadTareaDto;
import com.aplicacion.login.dto.TareaDto;
import com.aplicacion.login.dto.TareaUsuarioDto;
import com.aplicacion.login.service.ActividadTareaService;
import com.aplicacion.login.service.TareaAsignacionService;
import com.aplicacion.login.service.TareaService;

import lombok.RequiredArgsConstructor;
 
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class PanelController {
	private final TareaService tareaService;
	private final TareaAsignacionService tareaAsignacionService;
	private final ActividadTareaService actividadTareaService;

	@GetMapping("/tareas")
	public List<TareaDto> getAll(@CookieValue(name="jwt", required=false) String token) {
		return tareaService.getAll(token);
	}

	@PostMapping("/tareas")
	public TareaDto crearTarea(
			@RequestBody TareaDto request,
			@CookieValue(name="jwt", required=false) String token
			) {
		return tareaService.crearTarea(request, token);
	}

	@PutMapping("/tareas/{tareaId}")
	public TareaDto actualizarTarea(
			@PathVariable String tareaId,
			@RequestBody TareaDto request,
			@CookieValue(name="jwt", required=false) String token
			) {
		return tareaService.actualizarTarea(tareaId, request, token);
	}

	@DeleteMapping("/tareas/{tareaId}")
	public void borrarTarea(
			@PathVariable String tareaId,
			@CookieValue(name="jwt", required=false) String token
			) {
		tareaService.borrarTarea(tareaId, token);
	}

	@GetMapping("/tareas/{tareaId}/usuarios")
	public List<TareaUsuarioDto> getUsuariosAsignados(
			@PathVariable String tareaId,
			@CookieValue(name="jwt", required=false) String token
			) {
		return tareaAsignacionService.getUsuariosAsignados(tareaId, token);
	}

	@PostMapping("/tareas/{tareaId}/usuarios/{userId}")
	public List<TareaUsuarioDto> asignarUsuario(
			@PathVariable String tareaId,
			@PathVariable Long userId,
			@CookieValue(name="jwt", required=false) String token
			) {
		return tareaAsignacionService.asignarUsuario(tareaId, userId, token);
	}

	@DeleteMapping("/tareas/{tareaId}/usuarios/{userId}")
	public List<TareaUsuarioDto> quitarUsuario(
			@PathVariable String tareaId,
			@PathVariable Long userId,
			@CookieValue(name="jwt", required=false) String token
			) {
		return tareaAsignacionService.quitarUsuario(tareaId, userId, token);
	}

	@GetMapping("/tareas/{tareaId}/actividades")
	public List<ActividadTareaDto> getActividades(
			@PathVariable String tareaId,
			@CookieValue(name="jwt", required=false) String token
			) {
		return actividadTareaService.getActividades(tareaId, token);
	}

	@PostMapping("/tareas/{tareaId}/actividades")
	public ActividadTareaDto registrarActividad(
			@PathVariable String tareaId,
			@RequestBody ActividadTareaDto request,
			@CookieValue(name="jwt", required=false) String token
			) {
		return actividadTareaService.registrarActividad(tareaId, request, token);
	}
}
