package com.uniquindio.nexuscultural.application.dto.response;

public record AuthTokenResponse(
        String token,
        String tipo,
        String rol,
        long expiracionEnSegundos
) {}
