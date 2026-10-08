package com.uniquindio.nexuscultural.application.dto.request;

public record AutenticarUsuarioRequest(
        String usuario, //Aun por definir si es correo, nombre usuario o telefono
        String contrasena
) {}
