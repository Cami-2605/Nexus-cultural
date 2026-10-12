package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;

// Mapea a: Usuario.validarCodigoOTP()
public record ValidarOtpRequest(
        @NotBlank(message = "El telefono no debe estar vacio")
        String telefono,

        @NotBlank(message = "El código no debe estar vacio")
        String codigo
) {}
