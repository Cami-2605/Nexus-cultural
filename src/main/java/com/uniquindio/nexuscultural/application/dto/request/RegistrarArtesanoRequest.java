package com.uniquindio.nexuscultural.application.dto.request;

import java.util.List;

public record RegistrarArtesanoRequest(
        String nombreCompleto,
        String email,
        int anosExperiencia,
        List<String> reconocimientos,
        String departamento,
        String municipio,
        String direccion
) {}