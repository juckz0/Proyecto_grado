package com.aplicacion.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaDto {
	private String tarea_id;
	private String tarea_des;
	private String fecha_ini;
	private String fecha_fin;
	private String estado;
}
