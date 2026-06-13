package com.aplicacion.login.repository;

import com.aplicacion.login.entity.ActividadTarea;
import com.aplicacion.login.entity.ActividadTareaId;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadTareaRepository extends JpaRepository<ActividadTarea, ActividadTareaId> {
	List<ActividadTarea> findById_TareaId(String tareaId);
	List<ActividadTarea> findById_TareaIdAndId_UserId(String tareaId, Long userId);
}
