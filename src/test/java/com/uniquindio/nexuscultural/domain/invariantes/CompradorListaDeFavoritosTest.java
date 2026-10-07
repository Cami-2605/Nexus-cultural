package com.uniquindio.nexuscultural.domain.invariantes;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CompradorListaDeFavoritosTest {
    private final Email email = new Email("comprador@correo.com");
    private final UbicacionGeografica direccion = new UbicacionGeografica("Armenia", "Quindío", "Algúnlugar calle 225");

    private Comprador nuevoComprador() {
        return Comprador.registrar("Laura Gómez", email, direccion, LocalDateTime.now());
    }

    @Test
    @DisplayName("No permite agregar el mismo artículo dos veces a favoritos")
    void testInvarianteNoPermiteFavoritoDuplicado() {
        // Arrange
        Comprador comprador = nuevoComprador();
        UUID articuloId = UUID.randomUUID();
        comprador.agregarFavorito(articuloId);
        int tamanoOriginal = comprador.getFavoritos().size();

        // Act & Assert: la excepción esperada...
        assertThrows(ReglaDominioException.class, () -> comprador.agregarFavorito(articuloId));

        // ...y que la lista no haya crecido tras el rechazo
        assertEquals(tamanoOriginal, comprador.getFavoritos().size());
    }

    @Test
    @DisplayName("No se pueden añadir más de 15 artículos en la lista de favoritos")

    void testInvarianteMaximoQuinceFavoritos() {
        // Arrange: 15 favoritos ya registrados (el límite)
        Comprador comprador = nuevoComprador();
        for (int i = 0; i < 15; i++) {
            comprador.agregarFavorito(UUID.randomUUID());
        }
        int tamanoOriginal = comprador.getFavoritos().size();

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> comprador.agregarFavorito(UUID.randomUUID()));

        // La lista se queda exactamente en 15
        assertEquals(tamanoOriginal, comprador.getFavoritos().size());
        assertEquals(15, comprador.getFavoritos().size());
    }
}
