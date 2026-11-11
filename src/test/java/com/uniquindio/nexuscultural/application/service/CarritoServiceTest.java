package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.AgregarItemCarritoRequest;
import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CarritoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CompradorRepository compradorRepository;

    @InjectMocks
    private CarritoService carritoService;

    private UUID compradorId;
    private UUID articuloId;
    private Comprador compradorMock;

    @BeforeEach
    void setUp() {
        compradorMock = Comprador.registrar(
                "Comprador Prueba",
                new Email("comprador@correo.com"),
                new UbicacionGeografica("Quindío", "Armenia", "Calle 10"),
                java.time.LocalDateTime.now()
        );
        compradorId = compradorMock.getId();
        articuloId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Debe agregar un nuevo artículo al carrito exitosamente")
    void agregarItemNuevoExitoso() {
        AgregarItemCarritoRequest request = new AgregarItemCarritoRequest(articuloId, 2);

        when(compradorRepository.buscarPorId(compradorId)).thenReturn(Optional.of(compradorMock));
        when(carritoRepository.buscarPorComprador(compradorId)).thenReturn(List.of());
        when(carritoRepository.guardar(any(CarritoItem.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CarritoItem resultado = carritoService.agregarItem(compradorId, request);

        assertNotNull(resultado);
        assertEquals(compradorId, resultado.getCompradorId());
        assertEquals(articuloId, resultado.getArticuloId());
        assertEquals(2, resultado.getCantidad());
        verify(carritoRepository, times(1)).guardar(any(CarritoItem.class));
    }

    @Test
    @DisplayName("Debe consolidar cantidad si el artículo ya existía en el carrito")
    void agregarItemExistenteConsolidarCantidad() {
        AgregarItemCarritoRequest request = new AgregarItemCarritoRequest(articuloId, 3);
        CarritoItem itemExistente = new CarritoItem(compradorId, articuloId, 2);

        when(compradorRepository.buscarPorId(compradorId)).thenReturn(Optional.of(compradorMock));
        when(carritoRepository.buscarPorComprador(compradorId)).thenReturn(List.of(itemExistente));
        when(carritoRepository.actualizarCantidad(eq(itemExistente.getId()), eq(5)))
                .thenReturn(new CarritoItem(compradorId, articuloId, 5));

        CarritoItem resultado = carritoService.agregarItem(compradorId, request);

        assertNotNull(resultado);
        assertEquals(5, resultado.getCantidad());
        verify(carritoRepository, times(1)).actualizarCantidad(itemExistente.getId(), 5);
        verify(carritoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el comprador no existe al agregar ítem")
    void agregarItemCompradorNoExiste() {
        AgregarItemCarritoRequest request = new AgregarItemCarritoRequest(articuloId, 1);
        when(compradorRepository.buscarPorId(compradorId)).thenReturn(Optional.empty());

        assertThrows(ReglaDominioException.class, () -> carritoService.agregarItem(compradorId, request));
    }

    @Test
    @DisplayName("Debe obtener la lista de ítems del carrito de un comprador")
    void obtenerCarritoExitoso() {
        CarritoItem item1 = new CarritoItem(compradorId, UUID.randomUUID(), 1);
        CarritoItem item2 = new CarritoItem(compradorId, UUID.randomUUID(), 2);

        when(compradorRepository.buscarPorId(compradorId)).thenReturn(Optional.of(compradorMock));
        when(carritoRepository.buscarPorComprador(compradorId)).thenReturn(List.of(item1, item2));

        List<CarritoItem> resultado = carritoService.obtenerCarrito(compradorId);

        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Debe actualizar cantidad de un ítem perteneciente al comprador")
    void actualizarCantidadExitoso() {
        UUID itemId = UUID.randomUUID();
        CarritoItem item = new CarritoItem(compradorId, articuloId, 2);

        when(carritoRepository.buscarPorId(itemId)).thenReturn(Optional.of(item));
        when(carritoRepository.actualizarCantidad(itemId, 4)).thenReturn(new CarritoItem(compradorId, articuloId, 4));

        CarritoItem resultado = carritoService.actualizarCantidad(compradorId, itemId, 4);

        assertNotNull(resultado);
        assertEquals(4, resultado.getCantidad());
    }

    @Test
    @DisplayName("Debe fallar al actualizar si el ítem pertenece a otro comprador")
    void actualizarCantidadItemDeOtroComprador() {
        UUID itemId = UUID.randomUUID();
        UUID otroCompradorId = UUID.randomUUID();
        CarritoItem item = new CarritoItem(otroCompradorId, articuloId, 2);

        when(carritoRepository.buscarPorId(itemId)).thenReturn(Optional.of(item));

        assertThrows(ReglaDominioException.class, () -> carritoService.actualizarCantidad(compradorId, itemId, 4));
    }

    @Test
    @DisplayName("Debe eliminar un ítem del carrito exitosamente")
    void eliminarItemExitoso() {
        UUID itemId = UUID.randomUUID();
        CarritoItem item = new CarritoItem(compradorId, articuloId, 2);

        when(carritoRepository.buscarPorId(itemId)).thenReturn(Optional.of(item));
        doNothing().when(carritoRepository).eliminar(itemId);

        assertDoesNotThrow(() -> carritoService.eliminarItem(compradorId, itemId));
        verify(carritoRepository, times(1)).eliminar(itemId);
    }

    @Test
    @DisplayName("Debe vaciar el carrito de compras exitosamente")
    void vaciarCarritoExitoso() {
        when(compradorRepository.buscarPorId(compradorId)).thenReturn(Optional.of(compradorMock));
        doNothing().when(carritoRepository).eliminarPorComprador(compradorId);

        assertDoesNotThrow(() -> carritoService.vaciarCarrito(compradorId));
        verify(carritoRepository, times(1)).eliminarPorComprador(compradorId);
    }
}

