package com.aplicacion.login.config;



import com.aplicacion.login.component.UrlPermissionService;
import com.aplicacion.login.dto.ErrorResponse;
import com.aplicacion.login.exception.CustomAuthenticationException;
import com.aplicacion.login.jwt.service.JwtService;
import com.aplicacion.login.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtService jwtService;
	private final UserRepository userRepository;
	private final UrlPermissionService urlPermissionService;

	@Bean
	public UserDetailsService userDetailsService() {
		return username ->
		userRepository.findByUsername(username)
		.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		var provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtService, userDetailsService());
	}

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public AuthorizationManager<RequestAuthorizationContext> authorizationManager() {
		return (auth, context) -> {
			Authentication a = auth.get();
			// 1) Rechazar si no está autenticado
			if (a == null || !a.isAuthenticated() || a instanceof AnonymousAuthenticationToken) {
				return new AuthorizationDecision(false);
			}

			HttpServletRequest req = context.getRequest();
			String method  = req.getMethod();
			String uri     = req.getRequestURI();
			Collection<ConfigAttribute> attrs = urlPermissionService.getAttributes(req);
			System.out.printf(
					">> Autorizar %s %s → %d reglas encontradas: %s%n",
					method, uri, 
					attrs == null ? 0 : attrs.size(), 
							attrs
					);

			// 2) Si no hay atributos (lista vacía), DENEGAR
			if (attrs.isEmpty()) {
				return new AuthorizationDecision(false);
			}

			// 3) Comprobar que alguna autoridad del usuario coincide:
			Set<String> needed = attrs.stream()
					.map(ConfigAttribute::getAttribute)
					.collect(Collectors.toSet());

			boolean granted = a.getAuthorities().stream()
					.map(GrantedAuthority::getAuthority)
					.anyMatch(needed::contains);

			return new AuthorizationDecision(granted);
		};
	}



	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		ObjectMapper mapper = new ObjectMapper();

		http
		.cors(Customizer.withDefaults())
		.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.csrf(AbstractHttpConfigurer::disable)

		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/ws/**").permitAll() 
				.requestMatchers(HttpMethod.POST, "/api/auth/**", "/api/auth/logout","/api/notifications/**").permitAll()
				.requestMatchers(HttpMethod.GET,
						"/api/auth/me",
				//		"/api/auth/tareas",
					//	"/api/auth/tareas/**",
						"/api/auth/2fa/provision/**")
				.permitAll()
			//	.requestMatchers(HttpMethod.PUT, "/api/auth/tareas/**").permitAll()
				//.requestMatchers(HttpMethod.DELETE, "/api/auth/tareas/**").permitAll()

				// ¡Aquí llamamos al método, no inyectamos!
				.anyRequest().access(authorizationManager())
				)

		/*.exceptionHandling(ex -> ex
				.authenticationEntryPoint((req, res, exAuth) -> {
					res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					res.setContentType("application/json");
					res.getWriter().write("{\"error\":\"No estás autenticado\"}");
				})
				)*/
		.exceptionHandling(ex -> ex

				// 401: no autenticado o fallo auth
				.authenticationEntryPoint((HttpServletRequest req,
						HttpServletResponse res,
						AuthenticationException authEx) -> {

							String code;
							String desc;
							HttpStatus status;

							if (authEx instanceof CustomAuthenticationException cae) {
								code   = cae.getCode();
								desc   = cae.getDescription();
								status = cae.getStatus();
							} else {
								code   = "UNAUTHORIZED";
								desc   = authEx.getMessage() != null
										? authEx.getMessage()
												: "No estás autenticado";
								status = HttpStatus.UNAUTHORIZED;
							}

							res.setStatus(status.value());
							res.setContentType(MediaType.APPLICATION_JSON_VALUE);
							mapper.writeValue(
									res.getWriter(),
									new ErrorResponse(code, desc)
									);
						})

				// 403: autenticado pero sin permisos
				.accessDeniedHandler((req, res, deniedEx) -> {
					res.setStatus(HttpStatus.FORBIDDEN.value());
					res.setContentType(MediaType.APPLICATION_JSON_VALUE);
					mapper.writeValue(
							res.getWriter(),
							new ErrorResponse("ACCESS_DENIED", "No tienes permiso para este recurso")
							);
				})
				)

		.headers(headers -> headers.frameOptions(frame -> frame.disable()))

		.authenticationProvider(authenticationProvider())
		.addFilterBefore(jwtAuthenticationFilter(),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:4200"));
		config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		var source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring()
				.requestMatchers("/h2-console/**");
	}
}
