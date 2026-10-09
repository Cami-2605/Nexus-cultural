package com.uniquindio.nexuscultural.application.dto.request;

// Mapea a: Usuario.solicitarCodigoOTP()
public record AutenticarUsuarioRequest(
        String usuario, //Aun por definir si es correo, nombre usuario o telefono
        String contrasena
) {}
