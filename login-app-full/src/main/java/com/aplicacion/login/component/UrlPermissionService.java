package com.aplicacion.login.component;

import com.aplicacion.login.entity.Permission;
import com.aplicacion.login.repository.PermissionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UrlPermissionService {

	private final PermissionRepository permRepo;

	public UrlPermissionService(PermissionRepository permRepo) {
		this.permRepo = permRepo;
	}

	/**
	 * Por cada petición:
	 *  1) carga la lista actual de permisos de la BD
	 *  2) filtra por método y patrón ANT
	 *  3) convierte a ConfigAttribute
	 *  4) imprime en consola qué permisos había y cuáles casan
	 */
	public Collection<ConfigAttribute> getAttributes(HttpServletRequest request) {
		String method = request.getMethod();
		String uri    = request.getRequestURI();

		// 1) Leer todos los permisos
		List<Permission> all = permRepo.findAll();
		System.out.printf("➡️ Petición %s %s → %d permisos en BD%n",
				method, uri, all.size());

		// 2) Filtrar y mapear con CAST a ConfigAttribute
		var attrs = all.stream()
				// por método
				.filter(p -> p.getHttpMethod().equalsIgnoreCase(method))
				// por patrón Ant
				.filter(p -> {
					var matcher = new AntPathRequestMatcher(p.getUrlPattern(), method);
					boolean ok = matcher.matches(request);
					if (ok) {
						System.out.printf("   ✅ Patrón %s:%s coincide (permKey=%s)%n",
								p.getHttpMethod(), p.getUrlPattern(), p.getPermissionKey());
					}
					return ok;
				})
				// **AQUÍ**: casteamos a ConfigAttribute
				.map(p -> (ConfigAttribute)new SecurityConfig(p.getPermissionKey()))
				.collect(Collectors.toList());

		// 3) Log del total
		System.out.printf("   🎯 Total reglas aplicables: %d → %s%n",
				attrs.size(),
				attrs.stream()
				.map(ConfigAttribute::getAttribute)
				.collect(Collectors.toList())
				);

		return attrs;
	}

}
