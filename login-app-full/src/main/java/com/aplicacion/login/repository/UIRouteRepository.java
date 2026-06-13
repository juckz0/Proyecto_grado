package com.aplicacion.login.repository;

import com.aplicacion.login.entity.UIRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UIRouteRepository extends JpaRepository<UIRoute, Long> {
  // Para que tu controlador devuelva solo las rutas permitidas
  List<UIRoute> findByPermissionKeyIn(Set<String> keys);
}