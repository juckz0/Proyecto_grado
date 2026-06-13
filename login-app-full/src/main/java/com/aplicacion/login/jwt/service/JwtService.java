package com.aplicacion.login.jwt.service;



import com.aplicacion.login.entity.User;
import com.aplicacion.login.repository.UserRepository;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;                  // levanta la clave desde properties

    @Value("${jwt.expiration}")
    private long expirationMs;              // levanta el tiempo de expiración

    private Key key;                        // clave HMAC

    private final UserRepository userRepository;

    @PostConstruct
    public void init() {
        // Crea la Key HMAC una sola vez
        key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generar token JWT
    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Extraer cualquier claim
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return extractAllClaims(token).map(resolver).orElse(null);
    }

    // Intenta parsear el token y devolver los Claims
    private Optional<Claims> extractAllClaims(String token) {
        try {
            return Optional.of(
                Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
            );
        } catch (JwtException | IllegalArgumentException ex) {
            // token inválido/expirado/etc
            return Optional.empty();
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenExpired(String token) {
        Date exp = extractExpiration(token);
        return exp == null || exp.before(new Date());
    }

    // Valida el token para un usuario concreto
    public boolean isTokenValid(String token, User user) {
        String username = extractUsername(token);
        return username != null
            && username.equals(user.getUsername())
            && !isTokenExpired(token);
    }

    // NUEVO MÉTODO: valida con solo el token
    public boolean isTokenValid(String token) {
        // 1) Parsea y extrae username
        String username = extractUsername(token);
        if (username == null) return false;

        // 2) Carga el usuario
        return userRepository.findByUsername(username)
            .map(user -> isTokenValid(token, user))
            .orElse(false);
    }
}
