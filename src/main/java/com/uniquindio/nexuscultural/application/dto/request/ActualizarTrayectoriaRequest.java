package com.uniquindio.nexuscultural.application.dto.request;

public record ActualizarTrayectoriaRequest(
        String historia,
        String especialidad,
        int anosExperiencia
) {}