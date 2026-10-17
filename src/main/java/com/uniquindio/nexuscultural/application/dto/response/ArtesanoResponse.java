package com.uniquindio.nexuscultural.application.dto.response;

import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta con el perfil completo de un artesano, incluyendo sus logros de gamificación.
 */
public record ArtesanoResponse(
        UUID id,
        String nombreCompleto,
        String email,
        LocalDateTime fechaRegistro,
        boolean activo,
        TrayectoriaCultural trayectoriaCultural,
        UbicacionGeografica ubicacionGeografica,
        List<LogroResponse> logros
) {}
