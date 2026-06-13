package com.aplicacion.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActividadTareaDto {
	private Long user_id;
	private String tarea_id;
	private String actividad_desc;
	private Long tiempo_reg;
}
