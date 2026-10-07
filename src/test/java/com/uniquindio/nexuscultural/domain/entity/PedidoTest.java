package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PedidoTest {

    @Test
    @DisplayName("Dos Pedidos con el mismo ID representan la misma Entidad independientemente de su estado")
    void testIgualdadPorIdentidad() throws Exception {
        // Arrange: mismo ID, distinta cantidad y total
        UUID mismoId = UUID.randomUUID();
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        LocalDateTime fechaCreacion = LocalDateTime.now();
        Precio total1 = new Precio(new BigDecimal("60000"), "COP");
        Precio total2 = new Precio(new BigDecimal("150000"), "COP");

        Constructor<Pedido> constructor = Pedido.class.getDeclaredConstructor(
                UUID.class, UUID.class, UUID.class, int.class, Precio.class, LocalDateTime.class);
        constructor.setAccessible(true);

        Pedido pedido1 = constructor.newInstance(mismoId, compradorId, articuloId, 2, total1, fechaCreacion);
        Pedido pedido2 = constructor.newInstance(mismoId, compradorId, articuloId, 5, total2, fechaCreacion);

        // Act & Assert (igualdad por ID)
        assertEquals(pedido1, pedido2);
        assertEquals(pedido1.hashCode(), pedido2.hashCode());
    }

    @Test
    @DisplayName("Dos Pedidos con IDs diferentes son entidades distintas aunque tengan los mismos datos")
    void testDesigualdadPorIdentidadDiferente() {
        // Arrange: IDs diferentes, mismos datos
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        Precio precioUnitario = new Precio(new BigDecimal("30000"), "COP");
        LocalDateTime ahora = LocalDateTime.now();

        Pedido pedido1 = Pedido.crear(compradorId, articuloId, 2, precioUnitario, ahora);
        Pedido pedido2 = Pedido.crear(compradorId, articuloId, 2, precioUnitario, ahora);

        // Act & Assert
        assertNotEquals(pedido1, pedido2);
    }

    @Test
    @DisplayName("Un Pedido es igual a sí mismo")
    void testIgualdadReflexiva() {
        // Arrange
        UUID compradorId = UUID.randomUUID();
        UUID articuloId = UUID.randomUUID();
        Precio precioUnitario = new Precio(new BigDecimal("30000"), "COP");
        LocalDateTime ahora = LocalDateTime.now();

        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, precioUnitario, ahora);

        // Act & Assert
        assertEquals(pedido, pedido);
    }
}
