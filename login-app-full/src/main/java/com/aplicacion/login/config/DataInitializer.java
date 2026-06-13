package com.aplicacion.login.config;

import com.aplicacion.login.entity.Permission;
import com.aplicacion.login.entity.Role;
import com.aplicacion.login.entity.Tarea;
import com.aplicacion.login.entity.UIRoute;
import com.aplicacion.login.entity.User;
import com.aplicacion.login.repository.PermissionRepository;
import com.aplicacion.login.repository.RoleRepository;
import com.aplicacion.login.repository.TareaRepository;
import com.aplicacion.login.repository.UIRouteRepository;
import com.aplicacion.login.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base32;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

	private final PermissionRepository permissionRepo;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final RoleRepository roleRepo;
	private final TareaRepository tareaRepo;
	private final PermissionRepository permRepo;
	private final UIRouteRepository	uiRouteRepo;
	@Value("${app.admin.username}")
	private String adminUsername;

	@Value("${app.admin.password}")
	private String adminPassword;

	@Value("${app.admin.roles}")
	private String[] adminRoles;
	
	private String[] idTarea = {"1"};

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (permRepo.count() == 0) {
			permRepo.saveAll(List.of(
					Permission.builder()
					.urlPattern("/api/auth/users/**")
					.httpMethod("GET")
					.permissionKey("USER_VIEW")
					.build(),
					Permission.builder()
					.urlPattern("/api/auth/ui-routes")
					.httpMethod("GET")
					.permissionKey("UI_ROUTES")
					.build(),
					Permission.builder()
					.urlPattern("/api/auth/users/**")
					.httpMethod("PUT")
					.permissionKey("USER_EDIT")
					.build(),
					Permission.builder()
					.urlPattern("/api/auth/users/**")
					.httpMethod("DELETE")
					.permissionKey("USER_DELETE")
					.build()
					));
			System.out.println("👮‍♂️ Permisos iniciales creados");
		}
		// 2) Crea **directamente** cada rol con su Set<Permission>
		if (!roleRepo.existsById("ADMIN")) {
			Set<Permission> allPerms = new HashSet<>(permissionRepo.findAll());
			Role admin = Role.builder()
					.name("ADMIN")
					.permissions(allPerms)
					.build();
			roleRepo.save(admin);
		}

		if (!roleRepo.existsById("EDITOR")) {
			Permission view = permissionRepo.findByPermissionKey("USER_VIEW").orElseThrow();
			Permission edit = permissionRepo.findByPermissionKey("USER_EDIT").orElseThrow();
			Role editor = Role.builder()
					.name("EDITOR")
					.permissions(Set.of( edit,view))
					.build();
			roleRepo.save(editor);
		}

		if (!roleRepo.existsById("VIEWER")) {
			Permission view = permissionRepo.findByPermissionKey("USER_VIEW").orElseThrow();
			Role viewer = Role.builder()
					.name("VIEWER")
					.permissions(Set.of(view))
					.build();
			roleRepo.save(viewer);
		}

		if (!roleRepo.existsById("INICIAL")) {
			Role viewer = Role.builder()
					.name("INICIAL")
					.build();
			roleRepo.save(viewer);
		}
		
		 Tarea tarea = Tarea.builder()
         		.tarea_id("1")
         		.tarea_des("Descripcion")
         		.build();
         tareaRepo.save(tarea);
		// 2) Crea usuario “admin” con todos los roles
		if (userRepository.findByUsername(adminUsername).isEmpty()) {
			// Generar secreto TOTP aleatorio
			SecureRandom random = new SecureRandom();
			byte[] bytes = new byte[20];
			random.nextBytes(bytes);
			String totpSecret = new Base32().encodeToString(bytes).replace("=", "");

			// Convertir roles de String a Enum Role
			Set<Role> roles = Arrays.stream(adminRoles)
					.map(roleName -> roleRepo.findById(roleName)
							.orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + roleName)))
					.collect(Collectors.toSet());
            //
			Set<Tarea> tareas = Arrays.stream(idTarea)
					.map(tareaId -> tareaRepo.findById(tareaId)
							.orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + tareaId)))
					.collect(Collectors.toSet());
			// Crear entidad User con roles
			User admin = User.builder()
					.username(adminUsername)
					.password(passwordEncoder.encode(adminPassword))
					.roles(roles)
					.tareas(tareas)
					.totpSecret(totpSecret)
					.build();

			userRepository.save(admin);
           
            		
            		
            		
			System.out.println("=== Admin user created ===");
			System.out.printf(" username: %s%n", adminUsername);
			System.out.printf(" password: %s%n", adminPassword);
			System.out.printf(" TOTP secret stored. To configure 2FA, GET /api/auth/2fa/provision/%s%n", adminUsername);
		}

		// 3) Seed de UI-Routes si no existen
		if (uiRouteRepo.count() == 0) {
			uiRouteRepo.saveAll(List.of(
					UIRoute.builder()
					.path("users")
					.label("Usuarios")
					.icon("group")
					.permissionKey("USER_VIEW")
					.build()/*,
					UIRoute.builder()
					.path("users/edit")
					.label("Editar Usuario")
					.icon("edit")
					.permissionKey("USER_EDIT")
					.build(),
					UIRoute.builder()
					.path("users/delete")
					.label("Eliminar Usuario")
					.icon("delete")
					.permissionKey("USER_DELETE")
					.build()*/
					// …añade más según necesites
					));
			System.out.println("🚀 UI-Routes iniciales creadas");
		}
	}
}
