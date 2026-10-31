package com.uniquindio.nexuscultural.application.dto.response;

public record AuthTokenResponse(
        String token,
        String tipo,
        String rol,
        String nivelAcceso,
        long expiracionEnSegundos
) {
    public AuthTokenResponse(String token, String tipo, String rol, long expiracionEnSegundos) {
        this(token, tipo, rol, null, expiracionEnSegundos);
    }
}
