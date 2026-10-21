package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO para agregar un ítem al carrito de compras.
 */
public record AgregarItemCarritoRequest(
        @NotNull(message = "El ID del artículo es obligatorio")
        UUID articuloId,

        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad
) {}
