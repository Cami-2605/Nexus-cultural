package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.AgregarItemCarritoRequest;
import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CarritoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de aplicación para la gestión del carrito de compras.
 */
@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final CompradorRepository compradorRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          CompradorRepository compradorRepository) {
        this.carritoRepository = carritoRepository;
        this.compradorRepository = compradorRepository;
    }

    /** Agrega un artículo al carrito del comprador. Valida que el comprador exista. */
    public CarritoItem agregarItem(UUID compradorId, AgregarItemCarritoRequest request) {
        compradorRepository.buscarPorId(compradorId)
                .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado."));
        CarritoItem item = new CarritoItem(compradorId, request.articuloId(), request.cantidad());
        return carritoRepository.guardar(item);
    }

    /** Retorna todos los ítems del carrito de un comprador. */
    public List<CarritoItem> obtenerCarrito(UUID compradorId) {
        compradorRepository.buscarPorId(compradorId)
                .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado."));
        return carritoRepository.buscarPorComprador(compradorId);
    }

    /** Actualiza la cantidad de un ítem del carrito. Verifica que el ítem pertenezca al comprador. */
    public CarritoItem actualizarCantidad(UUID compradorId, UUID itemId, int nuevaCantidad) {
        CarritoItem item = carritoRepository.buscarPorId(itemId)
                .orElseThrow(() -> new ReglaDominioException("Ítem del carrito no encontrado."));
        if (!item.getCompradorId().equals(compradorId)) {
            throw new ReglaDominioException("El ítem no pertenece al comprador indicado.");
        }
        return carritoRepository.actualizarCantidad(itemId, nuevaCantidad);
    }

    /** Elimina un ítem del carrito. Verifica que el ítem pertenezca al comprador. */
    public void eliminarItem(UUID compradorId, UUID itemId) {
        CarritoItem item = carritoRepository.buscarPorId(itemId)
                .orElseThrow(() -> new ReglaDominioException("Ítem del carrito no encontrado."));
        if (!item.getCompradorId().equals(compradorId)) {
            throw new ReglaDominioException("El ítem no pertenece al comprador indicado.");
        }
        carritoRepository.eliminar(itemId);
    }

    /** Vacía todo el carrito de un comprador. */
    public void vaciarCarrito(UUID compradorId) {
        compradorRepository.buscarPorId(compradorId)
                .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado."));
        carritoRepository.eliminarPorComprador(compradorId);
    }
}
