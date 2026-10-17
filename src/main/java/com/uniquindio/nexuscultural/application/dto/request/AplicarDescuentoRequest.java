package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO para aplicar un descuento porcentual a un artículo.
 */
public record AplicarDescuentoRequest(
        @NotNull(message = "El ID del artesano es obligatorio")
        UUID artesanoId,

        @Min(value = 1, message = "El porcentaje mínimo es 1")
        @Max(value = 99, message = "El porcentaje máximo es 99")
        int porcentaje,

        @NotBlank(message = "El motivo del descuento es obligatorio")
        String motivo
) {}
