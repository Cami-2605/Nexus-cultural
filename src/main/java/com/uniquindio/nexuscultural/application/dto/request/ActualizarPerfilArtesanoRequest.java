package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilArtesanoRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100)
        String nombreCompleto,
        @NotBlank(message = "El email es obligatorio")
        @Size(min = 3, max = 100)
        String email
) {}