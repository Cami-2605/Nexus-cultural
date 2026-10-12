package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarTrayectoriaRequest(
        @NotBlank(message = "La historia es obligatoria")
        @Size(min = 10, max = 500)
        String historia,

        @NotBlank(message = "La especialidad es obligatoria")
        @Size(min = 3, max = 100)
        String especialidad,

        @NotBlank(message = "La experiencia es obligatoria")
        @Size(min = 3, max = 100)
        int anosExperiencia
) {}