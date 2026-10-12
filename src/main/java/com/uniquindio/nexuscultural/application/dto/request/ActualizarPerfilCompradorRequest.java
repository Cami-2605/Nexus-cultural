package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// ActualizarPerfilCompradorRequest.java
public record ActualizarPerfilCompradorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100)
        String nombreCompleto
) {}