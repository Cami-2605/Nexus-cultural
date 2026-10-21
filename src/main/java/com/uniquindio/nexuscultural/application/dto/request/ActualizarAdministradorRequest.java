package com.uniquindio.nexuscultural.application.dto.request;

import com.uniquindio.nexuscultural.domain.valueobject.NivelAcceso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ActualizarAdministradorRequest(
        @NotBlank(message = "El nombre es obligatorio") String nombreCompleto,
        @NotNull(message = "El nivel de acceso es obligatorio") NivelAcceso nivelAcceso
) {}
