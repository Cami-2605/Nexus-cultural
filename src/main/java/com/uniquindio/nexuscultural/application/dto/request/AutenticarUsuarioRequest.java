package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Mapea a: Usuario.solicitarCodigoOTP()
public record AutenticarUsuarioRequest(
        @NotBlank(message = "El nombre de usuario es obligatorio")
        @Size(min = 3, max = 100)
        String usuario, //Aun por definir si es correo, nombre usuario o telefono

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100)
        String contrasena
) {}
