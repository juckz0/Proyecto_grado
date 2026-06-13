package com.aplicacion.login.service;

import com.aplicacion.login.dto.AuthRequest;
import com.aplicacion.login.dto.AuthResponse;
import com.aplicacion.login.dto.RegisterRequest;
import com.aplicacion.login.entity.Role;
import com.aplicacion.login.entity.User;
import com.aplicacion.login.exception.ApiException;
import com.aplicacion.login.jwt.service.JwtService;
import com.aplicacion.login.repository.RoleRepository;
import com.aplicacion.login.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final TotpService totpService;
	private final RoleRepository   roleRepository;

	public AuthResponse register(RegisterRequest request) {
		// Generar secreto TOTP
		if (userRepository.existsByUsername(request.getUsername())) {
			throw new ApiException(
					"USER_ALREADY_EXISTS",          // tu código libre
					"El usuario '" + request.getUsername() + "' ya existe", // descripción
					HttpStatus.CONFLICT             // HTTP 409
					);
		}
		SecureRandom random = new SecureRandom();
		byte[] bytes = new byte[20];
		random.nextBytes(bytes);
		Base32 base32 = new Base32();
		String secret = base32.encodeToString(bytes).replace("=", "");

		Set<String> incoming = (request.getRoles() == null || request.getRoles().isEmpty())
				? Set.of("INICIAL")
						: request.getRoles();

		// 2) Convierte nombres de roles → entidades Role

		Set<Role> roleEntities = incoming.stream()
				.map(name -> roleRepository.findById(name)
						.orElseThrow(() -> new RuntimeException("Rol no encontrado: " + name)))
				.collect(Collectors.toSet());

		// 3) Crea el usuario con esos roles


		User user = User.builder()
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.correo(request.getCorreo())
				.primerNombre(request.getPrimerNombre())
				.segundoNombre(request.getSegundoNombre())
				.primerApellido(request.getPrimerApellido())
				.segundoApellido(request.getSegundoApellido())
				.roles(roleEntities)          // ya es Set<Role>
				.totpSecret(secret)
				.build();
		userRepository.save(user);

		// Retornar JWT
		String token = jwtService.generateToken(user);
		return new AuthResponse(token);
	}

	public AuthResponse login(AuthRequest request) throws InvalidKeyException {
		// 1) Validar credenciales
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getUsername(),
						request.getPassword()
						)
				);
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

		// 2) Validar código TOTP
		if (!totpService.verify(request.getTotpCode(), user.getTotpSecret())) {
			throw new BadCredentialsException("Código 2FA inválido");
		}

		// 3) Generar JWT si todo es válido
		String token = jwtService.generateToken(user);
		return new AuthResponse(token);
	}

	public boolean validateToken(String token) {
		return jwtService.isTokenValid(token);
	}

	public String getUsernameFromToken(String token) {
		return jwtService.extractUsername(token);
	}
}
