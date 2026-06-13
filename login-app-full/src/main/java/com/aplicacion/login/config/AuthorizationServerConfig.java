package com.aplicacion.login.config;

import com.nimbusds.jose.jwk.RSAKey;
import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class AuthorizationServerConfig {

  @Bean
  public RegisteredClientRepository registeredClientRepository(PasswordEncoder pw) {
    RegisteredClient angular = RegisteredClient.withId(UUID.randomUUID().toString())
      .clientId("angular-client")
      .clientSecret(pw.encode("angular-secret"))
      .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
      .redirectUri("http://localhost:4200")
      .postLogoutRedirectUri("http://localhost:4200")
      .scope(OidcScopes.OPENID)
      .scope("read")
      .clientSettings(ClientSettings.builder()
        .requireProofKey(true)
        .requireAuthorizationConsent(false)
        .build())
      .build();

    return new InMemoryRegisteredClientRepository(angular);
  }

  @Bean
  public JWKSource<SecurityContext> jwkSource() {
    RSAKey rsa = Jwks.generateRsa(); // Tu utilitaria para clave RSA
    JWKSet set = new JWKSet(rsa);
    return (selector, context) -> selector.select(set);
  }

  @Bean
  public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
    // Monta todos los endpoints de OAuth2 Authorization Server
    OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
    // Si quisieras deshabilitar CSRF solo para estos endpoints:
    http.csrf(csrf -> csrf.ignoringRequestMatchers("/oauth2/**"));
    return http.build();
  }
}
