package com.aplicacion.login.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TareaUsuarioDto {
	private Long id;
	private String username;
	private String correo;
}
