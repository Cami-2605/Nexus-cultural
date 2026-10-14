package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// Mapea a: Usuario.solicitarCodigoOTP()
public record SolicitarOtpRequest(
        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Debe ser un email válido")
        String email
) {}
