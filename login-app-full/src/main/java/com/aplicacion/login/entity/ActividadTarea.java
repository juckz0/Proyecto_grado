package com.aplicacion.login.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "actividades_tareas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActividadTarea {
	@EmbeddedId
	private ActividadTareaId id;

	@Column(name = "tiempo_reg", nullable = false)
	private Long tiempoReg;
}
