package com.aplicacion.login.repository;

import com.aplicacion.login.entity.Permission;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
	Optional<Permission> findByPermissionKey(String permissionKey);

}
