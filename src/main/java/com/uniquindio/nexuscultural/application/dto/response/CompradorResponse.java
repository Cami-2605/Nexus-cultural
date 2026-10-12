package com.uniquindio.nexuscultural.application.dto.response;

import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CompradorResponse(
        UUID id,
        String nombreCompleto,
        String email,
        LocalDateTime fechaRegistro,
        UbicacionGeografica direccionEntrega,
        List<UUID> favoritos
) {}