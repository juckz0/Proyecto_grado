package com.aplicacion.login.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ActividadTareaId implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id")
	private Long userId;

	@Column(name = "tarea_id", length = 50)
	private String tareaId;

	@Column(name = "actividad_desc", length = 4000)
	private String actividadDesc;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ActividadTareaId)) return false;
		ActividadTareaId that = (ActividadTareaId) o;
		return Objects.equals(userId, that.userId)
				&& Objects.equals(tareaId, that.tareaId)
				&& Objects.equals(actividadDesc, that.actividadDesc);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, tareaId, actividadDesc);
	}
}
