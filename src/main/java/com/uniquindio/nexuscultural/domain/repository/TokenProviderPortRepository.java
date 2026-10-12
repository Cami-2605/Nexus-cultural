package com.uniquindio.nexuscultural.domain.repository;

// Para gestionar token Jwt
public interface TokenProviderPortRepository {
    String generarToken(String usuarioId, String rol);
    void invalidarToken(String token);
}
