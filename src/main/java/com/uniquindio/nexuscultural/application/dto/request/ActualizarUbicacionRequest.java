package com.uniquindio.nexuscultural.application.dto.request;

public record ActualizarUbicacionRequest(
        String departamento,
        String municipio,
        String direccion
) {}