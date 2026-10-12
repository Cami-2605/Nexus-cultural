package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarUbicacionRequest(
        @NotBlank(message = "El departamento es obligatorio")
        @Size(min = 3, max = 100)
        String departamento,

        @NotBlank(message = "El municipio es obligatorio")
        @Size(min = 3, max = 100)
        String municipio,

        @NotBlank(message = "la dirección es obligatoria")
        @Size(min = 3, max = 100)
        String direccion
) {}