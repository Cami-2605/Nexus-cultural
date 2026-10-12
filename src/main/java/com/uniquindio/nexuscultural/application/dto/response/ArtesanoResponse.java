package com.uniquindio.nexuscultural.application.dto.response;

import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArtesanoResponse(
        UUID id,
        String nombreCompleto,
        String email,
        LocalDateTime fechaRegistro,
        boolean activo,
        TrayectoriaCultural trayectoriaCultural,
        UbicacionGeografica ubicacionGeografica
) {}