package com.uniquindio.nexuscultural.application.dto.request;

// Mapea a: Usuario.validarCodigoOTP()
public record ValidarOtpRequest(
        String telefono,
        String codigo
) {}
