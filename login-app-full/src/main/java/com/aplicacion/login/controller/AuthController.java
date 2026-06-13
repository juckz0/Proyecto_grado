package com.aplicacion.login.controller;

import lombok.RequiredArgsConstructor;

import java.security.InvalidKeyException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import com.aplicacion.login.dto.AuthRequest;
import com.aplicacion.login.dto.AuthResponse;
import com.aplicacion.login.dto.RegisterRequest;
import com.aplicacion.login.dto.UIRouteDto;
import com.aplicacion.login.entity.Permission;
import com.aplicacion.login.entity.User;
import com.aplicacion.login.repository.UIRouteRepository;
import com.aplicacion.login.repository.UserRepository;
import com.aplicacion.login.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final UserRepository userRepository;
	private final UIRouteRepository uiRouteRepo;

	/** 1) Registro: solo crea el usuario y devuelve el URI para el QR de 2FA */
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
		// this now saves user + totpSecret
		authService.register(request);

		// genera el provisioning URI
		String username = request.getUsername();
		String secret   = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"))
				.getTotpSecret();
		String uri = String.format(
				"otpauth://totp/%s:%s?secret=%s&issuer=%s",
				"MiApp", username, secret, "MiApp"
				);

		return ResponseEntity.ok(Map.of("provisioningUri", uri));
	}

	/** 2) Login: valida TOTP y emite la cookie JWT 
	 * @throws InvalidKeyException */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest request) throws InvalidKeyException {
		AuthResponse authResp = authService.login(request);
		String jwt = authResp.getToken();

		ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
				.httpOnly(true)
				.secure(false)              // en prod, true con HTTPS
				.sameSite("Lax")
				.path("/")
				.maxAge(Duration.ofHours(2))
				.build();

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(Map.of("status","ok"));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		// Creamos una cookie con el mismo nombre pero vacío y maxAge=0 para expirar
		ResponseCookie cookie = ResponseCookie.from("jwt", "")
				.httpOnly(true)
				.secure(false)              // en prod, true con HTTPS
				.path("/")
				.maxAge(0)                  // caduca inmediatamente
				.sameSite("Lax")
				.build();

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(Map.of("status", "logged out"));
	}


	/** 3) Provisioning (idéntico) */
	@GetMapping("/2fa/provision/{username}")
	public ResponseEntity<?> provision2fa(@PathVariable String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		String secret = user.getTotpSecret();
		String uri = String.format(
				"otpauth://totp/%s:%s?secret=%s&issuer=%s",
				"MiApp", username, secret, "MiApp"
				);
		return ResponseEntity.ok(Map.of("provisioningUri", uri));
	}

	/** 4) Me (como antes) */
	@GetMapping("/me")
	public ResponseEntity<?> me(@CookieValue(name="jwt", required=false) String token) {
		if (token == null || !authService.validateToken(token)) {
			return ResponseEntity.status(401).build();
		}
		String username = authService.getUsernameFromToken(token);
		User user = userRepository.findByUsername(username).orElseThrow();
		return ResponseEntity.ok(Map.of(
				"username", username,
				"roles", user.getRoles()
				));
	}

	@GetMapping("/ui-routes")
	public List<UIRouteDto> uiRoutes(
			@CookieValue("jwt") String token
			) {
		var username =  authService.getUsernameFromToken(token);
		var user = userRepository.findByUsername(username).orElseThrow();
		Set<String> perms = user.getRoles().stream()
				.flatMap(r -> r.getPermissions().stream())
				.map(Permission::getPermissionKey)
				.collect(Collectors.toSet());

		return uiRouteRepo.findByPermissionKeyIn(perms).stream()
				.map(r -> new UIRouteDto(r.getPath(), r.getLabel(), r.getIcon(),r.getPermissionKey()))
				.toList();
	}

}
