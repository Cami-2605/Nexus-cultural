package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.DevolverProductoRequest;
import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ArticuloRepository articuloRepository;

    @Mock
    private WompiService wompiService;

    @InjectMocks
    private PedidoService pedidoService;

    private UUID compradorId;
    private UUID artesanoId;
    private UUID articuloId;
    private Articulo articuloMock;
    private Precio precioMock;

    @BeforeEach
    void setUp() {
        compradorId = UUID.randomUUID();
        artesanoId = UUID.randomUUID();
        articuloId = UUID.randomUUID();
        precioMock = new Precio(new BigDecimal("100000"), "COP");

        articuloMock = Articulo.crear(
                artesanoId,
                "Mochila Wayuu",
                "Tejida por artesanos tradicionales",
                10,
                Grupo.TEJIDOS,
                List.of("https://s3.amazonaws.com/img1.jpg", "https://s3.amazonaws.com/img2.jpg"),
                precioMock,
                LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("Debe crear un pedido exitosamente y reducir el stock del artículo")
    void crearPedidoExitoso() {
        when(articuloRepository.buscarPorId(articuloId)).thenReturn(Optional.of(articuloMock));
        when(articuloRepository.guardar(any(Articulo.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(pedidoRepository).guardar(any(Pedido.class));

        Pedido pedido = pedidoService.crearPedido(compradorId, articuloId, 2);

        assertNotNull(pedido);
        assertEquals(compradorId, pedido.getCompradorId());
        assertEquals(articuloId, pedido.getArticuloId());
        assertEquals(2, pedido.getCantidad());
        assertEquals(8, articuloMock.getStock()); // 10 inicial - 2 = 8
        verify(articuloRepository, times(1)).guardar(articuloMock);
        verify(pedidoRepository, times(1)).guardar(any(Pedido.class));
    }

    @Test
    @DisplayName("Debe fallar al crear pedido si el artículo no existe")
    void crearPedidoArticuloNoExiste() {
        when(articuloRepository.buscarPorId(articuloId)).thenReturn(Optional.empty());

        assertThrows(ReglaDominioException.class, () -> pedidoService.crearPedido(compradorId, articuloId, 1));
    }

    @Test
    @DisplayName("Debe procesar el pago de un pedido exitosamente actualizando a CONFIRMADO")
    void procesarPagoExitoso() {
        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, precioMock, LocalDateTime.now());
        UUID pedidoId = pedido.getId();

        ProcesarPagoRequest request = new ProcesarPagoRequest(
                pedidoId,
                compradorId,
                precioMock,
                "WOMPI",
                "REF-WOMPI-12345"
        );

        when(pedidoRepository.obtenerPorId(pedidoId)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoRepository).guardar(pedido);

        Pedido resultado = pedidoService.procesarPago(request);

        assertNotNull(resultado);
        assertEquals(EstadoPedido.CONFIRMADO, resultado.getEstado());
        assertEquals("WOMPI", resultado.getMetodoPago());
        assertEquals("REF-WOMPI-12345", resultado.getReferenciaTransaccion());
        verify(pedidoRepository, times(1)).guardar(pedido);
    }

    @Test
    @DisplayName("Debe cancelar un pedido y reponer el stock al artículo")
    void cancelarPedidoExitoso() {
        Pedido pedido = Pedido.crear(compradorId, articuloId, 3, precioMock, LocalDateTime.now());
        UUID pedidoId = pedido.getId();

        when(pedidoRepository.obtenerPorId(pedidoId)).thenReturn(Optional.of(pedido));
        when(articuloRepository.buscarPorId(articuloId)).thenReturn(Optional.of(articuloMock));
        when(articuloRepository.guardar(any(Articulo.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(pedidoRepository).guardar(pedido);

        int stockAntes = articuloMock.getStock();
        Pedido resultado = pedidoService.cancelarPedido(pedidoId, compradorId);

        assertEquals(EstadoPedido.CANCELADO, resultado.getEstado());
        assertEquals(stockAntes + 3, articuloMock.getStock());
        verify(articuloRepository, times(1)).guardar(articuloMock);
        verify(pedidoRepository, times(1)).guardar(pedido);
    }

    @Test
    @DisplayName("Debe solicitar la devolución de un pedido entregado")
    void solicitarDevolucionExitosa() {
        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, precioMock, LocalDateTime.now());
        pedido.confirmar();
        pedido.enviar();
        pedido.marcarEntregado(LocalDateTime.now());
        UUID pedidoId = pedido.getId();

        DevolverProductoRequest request = new DevolverProductoRequest(
                pedidoId,
                compradorId,
                "Defecto en costura",
                "Costura rota al recibirlo"
        );

        when(pedidoRepository.obtenerPorId(pedidoId)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoRepository).guardar(pedido);

        Devolucion devolucion = pedidoService.solicitarDevolucion(request);

        assertNotNull(devolucion);
        assertEquals(EstadoDevolucion.EN_PROCESO, devolucion.getEstado());
        assertEquals("Defecto en costura", devolucion.getMotivo());
    }

    @Test
    @DisplayName("Debe resolver la devolución aprobada reponiendo stock")
    void resolverDevolucionAprobada() {
        Pedido pedido = Pedido.crear(compradorId, articuloId, 2, precioMock, LocalDateTime.now());
        pedido.confirmar();
        pedido.enviar();
        pedido.marcarEntregado(LocalDateTime.now());
        pedido.solicitarDevolucion(compradorId, "Pieza no corresponde", LocalDateTime.now());
        UUID pedidoId = pedido.getId();

        when(pedidoRepository.obtenerPorId(pedidoId)).thenReturn(Optional.of(pedido));
        when(articuloRepository.buscarPorId(articuloId)).thenReturn(Optional.of(articuloMock));
        when(articuloRepository.guardar(any(Articulo.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(pedidoRepository).guardar(pedido);

        int stockPrevio = articuloMock.getStock();
        Pedido resultado = pedidoService.resolverDevolucion(pedidoId, true);

        assertEquals(EstadoPedido.DEVUELTO, resultado.getEstado());
        assertEquals(stockPrevio + 2, articuloMock.getStock());
    }

    @Test
    @DisplayName("Debe avanzar el estado del pedido en el ciclo de vida")
    void avanzarEstadoPedido() {
        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, precioMock, LocalDateTime.now());
        UUID pedidoId = pedido.getId();

        when(pedidoRepository.obtenerPorId(pedidoId)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoRepository).guardar(pedido);

        // De PENDIENTE a CONFIRMADO
        Pedido avanzado = pedidoService.avanzarEstado(pedidoId);
        assertEquals(EstadoPedido.CONFIRMADO, avanzado.getEstado());

        // De CONFIRMADO a EN_CAMINO
        avanzado = pedidoService.avanzarEstado(pedidoId);
        assertEquals(EstadoPedido.EN_CAMINO, avanzado.getEstado());
    }

    @Test
    @DisplayName("Debe obtener un pedido existente por ID")
    void obtenerPorIdExitoso() {
        Pedido pedido = Pedido.crear(compradorId, articuloId, 1, precioMock, LocalDateTime.now());
        UUID pedidoId = pedido.getId();

        when(pedidoRepository.obtenerPorId(pedidoId)).thenReturn(Optional.of(pedido));

        Pedido encontrado = pedidoService.obtenerPorId(pedidoId);
        assertNotNull(encontrado);
        assertEquals(pedidoId, encontrado.getId());
    }
}
