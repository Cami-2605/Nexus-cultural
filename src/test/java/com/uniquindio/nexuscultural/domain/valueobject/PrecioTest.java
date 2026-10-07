package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PrecioTest {
    @Test
    void dosPreciosConElMismoValorDebenSerIguales() {
        // Arrange
        Precio p1 = new Precio(new BigDecimal("45000"), "COP");
        Precio p2 = new Precio(new BigDecimal("45000"), "COP");


        assertEquals(p1, p2);
    }
    @Test
    void noDebePermitirCrearUnPrecioConMontoNegativo() {
        // Arrange
        BigDecimal montoNegativo = new BigDecimal("-1000");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> new Precio(montoNegativo, "COP"));
    }


}
