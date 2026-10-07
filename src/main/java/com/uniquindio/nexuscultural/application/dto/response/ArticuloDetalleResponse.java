package com.uniquindio.nexuscultural.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

// Usado en GET /articulos/{id}
public record ArticuloDetalleResponse(
        String id,
        String artesanoId,
        String titulo,
        String descripcion,
        double montoPrecio,
        String moneda,
        int stock,
        String estado,
        String grupo,
        List<String> imagenes,
        LocalDateTime fechaPublicacion
) {}
