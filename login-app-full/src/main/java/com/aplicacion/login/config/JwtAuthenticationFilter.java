package com.aplicacion.login.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.aplicacion.login.jwt.service.JwtService;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	// rutas que no requieren JWT
	private static final List<String> EXCLUDE_URLS = List.of(
			"/api/auth/login",
			"/api/auth/register",
			"/api/notifications",          // excluye el prefijo
			"/api/notifications/"
			// …otros endpoints públicos…
			);

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(
			HttpServletRequest request,
			HttpServletResponse response,
			FilterChain chain
			) throws ServletException, IOException {

		// 1) Si la URI comienza con alguno de los prefijos, saltamos el filtro
		String path = request.getRequestURI();
		
		for (String exclude : EXCLUDE_URLS) {
			if (path.startsWith(exclude)) {
				chain.doFilter(request, response);
				return;
			}
		}
		// 1) LOG para ver que el filtro se dispara
		System.out.printf(" JwtFilter arrancó para %s %s%n",
				request.getMethod(), request.getRequestURI());
		// 1) Extraemos el token de la cookie "jwt"
		String token = null;
		if (request.getCookies() != null) {
			for (Cookie c : request.getCookies()) {
				if ("jwt".equals(c.getName())) {
					token = c.getValue();
					System.out.println("    Token encontrado en cookie: " 
							+ token.substring(0, 10) + "…");
					break;
				}
			}
		}

		// 2) Si tenemos token y el contexto aún no está autenticado
		if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			String username = jwtService.extractUsername(token);
			if (username != null) {
				var userDetails = userDetailsService.loadUserByUsername(username);
				// 3) Si el token es válido para ese usuario
				if (jwtService.isTokenValid(token, (com.aplicacion.login.entity.User) userDetails)) {
					var auth = new UsernamePasswordAuthenticationToken(
							userDetails,
							null,
							userDetails.getAuthorities()
							);
					auth.setDetails(
							new WebAuthenticationDetailsSource().buildDetails(request)
							);
					SecurityContextHolder.getContext().setAuthentication(auth);
					System.out.println("   Autenticación establecida para " + username);
				}
			}
		}

		// 4) Continuar con la cadena de filtros
		chain.doFilter(request, response);
	}
}
