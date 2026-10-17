package com.uniquindio.nexuscultural.application.dto.response;

import java.util.UUID;

/**
 * DTO de respuesta para un ítem del carrito de compras.
 */
public record CarritoItemResponse(
        UUID id,
        UUID compradorId,
        UUID articuloId,
        int cantidad
) {}
