package com.aplicacion.login.dto;




import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UIRouteDto {

	  private String path; 
	  private String label;         // p.ej. "Usuarios"
	  private String icon;          // p.ej. "group"
	  private String permissionKey; // p.ej. "USER_VIEW"
	 
}
