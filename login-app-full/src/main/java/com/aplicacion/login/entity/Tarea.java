package com.aplicacion.login.entity;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "tareas")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tarea {
	@Id
	@Column(name="tarea_id", length=50)
	private String tarea_id;
	
	@Column(name="tarea_des", length=500)
	private String tarea_des;
	
	@Column(name="fecha_ini")
	private Date fecha_ini;
	
	@Column(name="fecha_fin")
	private Date fecha_fin;
	
	@Column(name="estado", length=500)
	private String estado;

}

