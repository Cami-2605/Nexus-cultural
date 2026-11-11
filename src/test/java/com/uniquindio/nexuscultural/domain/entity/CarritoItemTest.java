package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarritoItemTest {

    @Test
    @DisplayName("Debe crear un ítem de carrito con datos válidos")
    void crearItemExitoso() {
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();

        CarritoItem item = new CarritoItem(compradorId, articuloId, 3);

        assertNotNull(item);
        assertNotNull(item.getId());
        assertEquals(compradorId, item.getCompradorId());
        assertEquals(articuloId, item.getArticuloId());
        assertEquals(3, item.getCantidad());
    }

    @Test
    @DisplayName("Debe fallar si el comprador es nulo")
    void crearItemCompradorNulo() {
        UUID articuloId = UUID.randomUUID();
        assertThrows(ReglaDominioException.class, () -> new CarritoItem(null, articuloId, 1));
    }

    @Test
    @DisplayName("Debe fallar si el artículo es nulo")
    void crearItemArticuloNulo() {
        UUID compradorId = UUID.randomUUID();
        assertThrows(ReglaDominioException.class, () -> new CarritoItem(compradorId, null, 1));
    }

    @Test
    @DisplayName("Debe fallar si la cantidad es menor o igual a cero")
    void crearItemCantidadInvalida() {
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        assertThrows(ReglaDominioException.class, () -> new CarritoItem(compradorId, articuloId, 0));
        assertThrows(ReglaDominioException.class, () -> new CarritoItem(compradorId, articuloId, -2));
    }

    @Test
    @DisplayName("Debe actualizar la cantidad correctamente cuando es válida")
    void actualizarCantidadExitosa() {
        CarritoItem item = new CarritoItem(UUID.randomUUID(), UUID.randomUUID(), 2);
        item.actualizarCantidad(5);
        assertEquals(5, item.getCantidad());
    }

    @Test
    @DisplayName("Debe fallar al actualizar con cantidad menor o igual a cero")
    void actualizarCantidadInvalida() {
        CarritoItem item = new CarritoItem(UUID.randomUUID(), UUID.randomUUID(), 2);
        assertThrows(ReglaDominioException.class, () -> item.actualizarCantidad(0));
        assertThrows(ReglaDominioException.class, () -> item.actualizarCantidad(-1));
    }

    @Test
    @DisplayName("Debe reconstruir un ítem correctamente mediante factory method")
    void reconstruirExitoso() {
        UUID id = UUID.randomUUID();
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();

        CarritoItem item = CarritoItem.reconstruir(id, compradorId, articuloId, 4);

        assertEquals(id, item.getId());
        assertEquals(compradorId, item.getCompradorId());
        assertEquals(articuloId, item.getArticuloId());
        assertEquals(4, item.getCantidad());
    }
}

