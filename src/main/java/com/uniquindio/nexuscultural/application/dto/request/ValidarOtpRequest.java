package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Mapea a: Usuario.validarCodigoOTP()
public record ValidarOtpRequest(
        @NotBlank(message = "El email no debe estar vacío")
        @Email(message = "Debe ser un email válido")
        String email,

        @NotBlank(message = "El código no debe estar vacío")
        String codigo
) {}