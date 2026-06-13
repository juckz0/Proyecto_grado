package com.aplicacion.login.service;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.aplicacion.login.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	/**
	 * Elimina un usuario por su ID. Lanza excepción si no existe.
	 */
	@Transactional
	public void deleteUser(Long id) {
		if (!userRepository.existsById(id)) {
			throw new UsernameNotFoundException("Usuario no encontrado con id: " + id);
		}
		userRepository.deleteById(id);
	}
}