package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.CarritoItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de acceso al repositorio del carrito de compras.
 */
public interface CarritoRepository {

    /** Persiste un ítem y lo retorna. */
    CarritoItem guardar(CarritoItem item);

    /** Retorna todos los ítems del carrito de un comprador. */
    List<CarritoItem> buscarPorComprador(UUID compradorId);

    /** Busca un ítem por su ID. */
    Optional<CarritoItem> buscarPorId(UUID itemId);

    /** Elimina un ítem específico. */
    void eliminar(UUID itemId);

    /** Elimina todos los ítems del carrito de un comprador. */
    void eliminarPorComprador(UUID compradorId);

    /** Actualiza la cantidad de un ítem existente y lo retorna. */
    CarritoItem actualizarCantidad(UUID itemId, int nuevaCantidad);
}
