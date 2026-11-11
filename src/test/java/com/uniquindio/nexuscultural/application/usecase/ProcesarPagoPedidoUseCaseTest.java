package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcesarPagoPedidoUseCaseTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ProcesarPagoPedidoUseCase procesarPagoPedidoUseCase;

    private UUID pedidoId;
    private UUID compradorId;
    private UUID articuloId;
    private Pedido pedidoMock;
    private Precio precioMock;

    @BeforeEach
    void setUp() {
        compradorId = UUID.randomUUID();
        articuloId = UUID.randomUUID();
        precioMock = new Precio(new BigDecimal("50000"), "COP");
        pedidoMock = Pedido.crear(compradorId, articuloId, 1, precioMock, LocalDateTime.now());
        pedidoId = pedidoMock.getId();
    }

    @Test
    @DisplayName("Debe procesar el pago y actualizar el pedido a estado CONFIRMADO")
    void ejecutarPagoExitoso() {
        ProcesarPagoRequest request = new ProcesarPagoRequest(
                pedidoId,
                compradorId,
                precioMock,
                "WOMPI",
                "REF-TRANS-987"
        );

        when(pedidoRepository.obtenerPorId(pedidoId)).thenReturn(Optional.of(pedidoMock));
        doNothing().when(pedidoRepository).guardar(pedidoMock);

        procesarPagoPedidoUseCase.ejecutar(request);

        assertEquals(EstadoPedido.CONFIRMADO, pedidoMock.getEstado());
        assertEquals("WOMPI", pedidoMock.getMetodoPago());
        assertEquals("REF-TRANS-987", pedidoMock.getReferenciaTransaccion());
        verify(pedidoRepository, times(1)).guardar(pedidoMock);
    }

    @Test
    @DisplayName("Debe fallar si el pedido no existe")
    void ejecutarPedidoNoExiste() {
        ProcesarPagoRequest request = new ProcesarPagoRequest(
                pedidoId,
                compradorId,
                precioMock,
                "WOMPI",
                "REF-TRANS-987"
        );

        when(pedidoRepository.obtenerPorId(pedidoId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> procesarPagoPedidoUseCase.ejecutar(request));
        verify(pedidoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe fallar si el comprador que paga no es el dueño del pedido")
    void ejecutarCompradorDiferente() {
        UUID otroCompradorId = UUID.randomUUID();
        ProcesarPagoRequest request = new ProcesarPagoRequest(
                pedidoId,
                otroCompradorId,
                precioMock,
                "WOMPI",
                "REF-TRANS-987"
        );

        when(pedidoRepository.obtenerPorId(pedidoId)).thenReturn(Optional.of(pedidoMock));

        assertThrows(IllegalStateException.class, () -> procesarPagoPedidoUseCase.ejecutar(request));
        verify(pedidoRepository, never()).guardar(any());
    }
}
