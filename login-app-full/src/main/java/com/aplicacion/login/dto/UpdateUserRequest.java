package com.aplicacion.login.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UpdateUserRequest {
    private String username;
    private String correo;
	private String primerNombre;
	private String segundoNombre;
	private String primerApellido;
	private String segundoApellido;
    private Set<String> roles;      // nombres de roles nuevos
}