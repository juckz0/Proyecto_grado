package com.aplicacion.login.jwt.service;

import org.springframework.stereotype.Service;

import com.aplicacion.login.entity.Role;
import com.aplicacion.login.entity.User;
import com.aplicacion.login.repository.RoleRepository;
import com.aplicacion.login.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {
  private final UserRepository userRepo;
  private final RoleRepository roleRepo;

  public void assignRole(String username, String roleName) {
    User u = userRepo.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    // Recupera la ENTIDAD Role por su PK (el nombre)
    Role roleEntity = roleRepo.findById(roleName)
        .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + roleName));
    u.getRoles().add(roleEntity);
    userRepo.save(u);
  }
}
