package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value object que representa un descuento porcentual aplicable a un artículo.
 * El porcentaje debe estar entre 1 y 99 (inclusive).
 */
public record Descuento(int porcentaje, String motivoDescuento) {

    public Descuento {
        if (porcentaje < 1 || porcentaje > 99) {
            throw new ReglaDominioException("El porcentaje de descuento debe estar entre 1 y 99.");
        }
        if (motivoDescuento == null || motivoDescuento.isBlank()) {
            throw new ReglaDominioException("El motivo del descuento es obligatorio.");
        }
    }

    /**
     * Aplica el descuento al precio dado y retorna un nuevo {@link Precio} con el monto reducido.
     * El resultado se redondea a 2 decimales con {@code HALF_UP}.
     */
    public Precio aplicar(Precio precio) {
        if (precio == null) throw new ReglaDominioException("El precio no puede ser nulo.");
        BigDecimal factor = BigDecimal.ONE
                .subtract(BigDecimal.valueOf(porcentaje).divide(BigDecimal.valueOf(100)));
        BigDecimal nuevoMonto = precio.monto().multiply(factor).setScale(2, RoundingMode.HALF_UP);
        return new Precio(nuevoMonto, precio.moneda());
    }
}
