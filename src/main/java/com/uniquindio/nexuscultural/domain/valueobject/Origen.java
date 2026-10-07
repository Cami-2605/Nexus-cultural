package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;


public record Origen(
        String municipio,
        String departamento,
        String pueblo
) {

    public Origen {
        if (municipio == null || municipio.isBlank()) {
            throw new ReglaDominioException(
                    "El municipio no puede estar vacío"
            );
        }

        if (departamento == null || departamento.isBlank()) {
            throw new ReglaDominioException(
                    "El departamento no puede estar vacío"
            );
        }

        if (pueblo == null || pueblo.isBlank()) {
            throw new ReglaDominioException(
                    "El pueblo no puede estar vacío"
            );
        }
    }
}