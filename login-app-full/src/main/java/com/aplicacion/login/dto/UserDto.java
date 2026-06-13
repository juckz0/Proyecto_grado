package com.aplicacion.login.dto;

import java.util.Set;

import com.aplicacion.login.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
	private Long id;
	private String username;
	private String correo;
	private String primerNombre;
	private String segundoNombre;
	private String primerApellido;
	private String segundoApellido;
	private Set<Role> roles; 


}
