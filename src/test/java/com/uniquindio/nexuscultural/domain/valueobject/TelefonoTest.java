package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelefonoTest {

    @Test
    @DisplayName("Debe crear un teléfono válido y formatear a salida internacional")
    void debeCrearTelefonoValido() {
        Telefono telefono = new Telefono("3001234567");

        assertNotNull(telefono);
        assertEquals("+573001234567", telefono.obtenerFormatoInternacional());
    }

    @Test
    @DisplayName("No debe permitir teléfonos con formato erróneo")
    void noDebeCrearTelefonoInvalido() {
        assertThrows(ReglaDominioException.class, () -> new Telefono("12345"));
        assertThrows(ReglaDominioException.class, () -> new Telefono("texto"));
        assertThrows(ReglaDominioException.class, () -> new Telefono(""));
    }
}
