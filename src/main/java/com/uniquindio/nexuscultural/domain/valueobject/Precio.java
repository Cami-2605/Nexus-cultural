package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

import java.math.BigDecimal;

public record Precio(BigDecimal monto, String moneda) {

    public Precio {
        if (monto == null || monto.signum() < 0) {
            throw new ReglaDominioException("El precio no puede ser negativo.");
        }
        if (moneda == null || moneda.isBlank()) {
            throw new ReglaDominioException("El precio debe indicar una moneda.");
        }
    }
}
