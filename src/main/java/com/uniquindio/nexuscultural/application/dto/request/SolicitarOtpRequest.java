package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;

// Mapea a: Usuario.solicitarCodigoOTP()
public record SolicitarOtpRequest(
        @NotBlank(message = "El telefono no debe estar vacio")
        String telefono
) {}
