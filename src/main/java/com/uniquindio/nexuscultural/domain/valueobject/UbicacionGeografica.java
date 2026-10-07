package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

public record UbicacionGeografica(
        String departamento,
        String municipio,
        String direccion
) {
    public UbicacionGeografica {
        if (departamento == null || departamento.isBlank()) {
            throw new ReglaDominioException("El departamento no puede estar vacío");
        }
        if (municipio == null || municipio.isBlank()) {
            throw new ReglaDominioException("El municipio no puede estar vacío");
        }
        if (direccion == null || direccion.isBlank()) {
            throw new ReglaDominioException("La dirección no puede estar vacía");
        }
    }
}
