package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Min;

/**
 * DTO para actualizar la cantidad de un ítem en el carrito.
 */
public record ActualizarCantidadItemRequest(
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad
) {}
