package com.uniquindio.nexuscultural.domain.invariantes;

import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoAgregadoTest {

    private final UUID compradorId = UUID.randomUUID();
    private final UUID articuloId = UUID.randomUUID();
    private final Precio precioUnitario = new Precio(new BigDecimal("30000"), "COP");

    /** Lleva un pedido hasta ENTREGADO usando el flujo público real, sin reflexión. */
    private Pedido pedidoEntregado(LocalDateTime fechaEntrega) {
        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, precioUnitario, LocalDateTime.now());
        pedido.confirmar();
        pedido.enviar();
        pedido.marcarEntregado(fechaEntrega);
        return pedido;
    }

    @Test
    @DisplayName("Pedido PENDIENTE no permite devolución")
    void testInvariantePedidoPendiente() {
        // Arrange
        LocalDateTime ahora = LocalDateTime.now();
        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, precioUnitario, ahora);
        EstadoPedido estadoOriginal = pedido.getEstado();

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.solicitarDevolucion(compradorId, "Producto defectuoso", ahora));
        assertEquals(estadoOriginal, pedido.getEstado());
    }

    @Test
    @DisplayName("Solo el comprador puede solicitar devolución")
    void testInvarianteAutorizacionDevolucion() {
        // Arrange
        LocalDateTime fechaEntrega = LocalDateTime.now().minusDays(1);
        Pedido pedido = pedidoEntregado(fechaEntrega);
        UUID otroUsuarioId = UUID.randomUUID();
        EstadoPedido estadoOriginal = pedido.getEstado();

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.solicitarDevolucion(otroUsuarioId, "Producto defectuoso", LocalDateTime.now()));
        assertEquals(estadoOriginal, pedido.getEstado());
    }

    @Test
    @DisplayName("No permite devolución fuera del plazo")
    void testInvariantePlazoDevolucion() {
        // Arrange: entregado hace 10 días, el plazo es de 8
        LocalDateTime fechaEntrega = LocalDateTime.now().minusDays(10);
        Pedido pedido = pedidoEntregado(fechaEntrega);
        EstadoPedido estadoOriginal = pedido.getEstado();

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.solicitarDevolucion(compradorId, "Producto defectuoso", LocalDateTime.now()));
        assertEquals(estadoOriginal, pedido.getEstado());
    }

    @Test
    @DisplayName("No permite crear pedido con cantidad inválida")
    void testInvarianteCreacionPedido() {
        // Arrange
        LocalDateTime ahora = LocalDateTime.now();

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> Pedido.crear(compradorId, articuloId, 0, precioUnitario, ahora));
    }

    @Test
    @DisplayName("No permite una segunda devolución mientras la primera está en proceso")
    void testInvarianteUnaSolaDevolucionActiva() {
        // Arrange
        LocalDateTime ahora = LocalDateTime.now();
        Pedido pedido = pedidoEntregado(ahora);
        Devolucion primera = pedido.solicitarDevolucion(compradorId, "No me gustó", ahora.plusDays(1));

        // Act & Assert
        assertThrows(ReglaDominioException.class,
                () -> pedido.solicitarDevolucion(compradorId, "Cambié de opinión", ahora.plusDays(2)));
        assertEquals(EstadoPedido.EN_DEVOLUCION, pedido.getEstado());
        assertEquals(primera, pedido.getDevolucion());
    }

    @Test
    @DisplayName("Al rechazar la devolución, el pedido vuelve a ENTREGADO")
    void testInvarianteConsistenciaAlResolverDevolucion() {
        // Arrange
        LocalDateTime ahora = LocalDateTime.now();
        Pedido pedido = pedidoEntregado(ahora);
        pedido.solicitarDevolucion(compradorId, "No era lo que esperaba", ahora.plusDays(1));

        // Act
        pedido.resolverDevolucion(false);

        // Assert: Pedido y Devolucion quedan consistentes entre sí
        assertEquals(EstadoPedido.ENTREGADO, pedido.getEstado());
        assertEquals(EstadoDevolucion.RECHAZADO, pedido.getDevolucion().getEstado());
    }
}



