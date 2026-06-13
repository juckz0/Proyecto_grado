package com.aplicacion.login.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aplicacion.login.entity.Role;

public interface RoleRepository extends JpaRepository<Role, String> {}
