package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmailTest {

    @Test
    void dosCorreosConElMismoTextoDebenSerIguales() {
        Email email1 = new Email("usuario@example.com");
        Email email2 = new Email("usuario@example.com");
        assertEquals(email1, email2);
    }

    @Test
    void noDebeCrearCorreoInvalido() {
        //trata de ejecutar el lambda y si lanza exactamente esa exception, la prueba pasa
        //Si no lanza nada o si lanza una exception distinta, la prueba falla
        assertThrows(ReglaDominioException.class, () -> new Email("correo.com"));
    }
}
