package com.uniquindio.nexuscultural.infraestructure.security;

import com.uniquindio.nexuscultural.application.dto.response.AuthTokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProviderAdapter {

    private final SecretKey key;
    private final long expirationInMs = 86400000; // 24 horas

    // ✅ AHORA: Lee directamente la variable 'jwt.secret' sin valor por defecto quemado
    public JwtTokenProviderAdapter(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public AuthTokenResponse generarToken(UUID usuarioId, String email, String rol) {
        return generarToken(usuarioId, email, rol, null);
    }

    public AuthTokenResponse generarToken(UUID usuarioId, String email, String rol, String nivelAcceso) {
        return generarToken(usuarioId, email, rol, nivelAcceso, null);
    }

    public AuthTokenResponse generarToken(UUID usuarioId, String email, String rol, String nivelAcceso, String nombre) {
        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + expirationInMs);

        var builder = Jwts.builder()
                .subject(email)
                .claim("id", usuarioId.toString())
                .claim("rol", rol);

        if (nivelAcceso != null) {
            builder.claim("nivelAcceso", nivelAcceso);
        }

        if (nombre != null && !nombre.isBlank()) {
            builder.claim("nombre", nombre);
        }

        String token = builder
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(key)
                .compact();

        return new AuthTokenResponse(token, "Bearer", rol, nivelAcceso, nombre, expirationInMs / 1000);
    }
}