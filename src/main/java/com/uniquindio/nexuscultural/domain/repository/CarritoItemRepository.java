package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.CarritoItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarritoItemRepository {

    CarritoItem guardar(CarritoItem item);

    Optional<CarritoItem> buscarPorId(UUID id);

    List<CarritoItem> buscarPorCompradorId(UUID compradorId);

    Optional<CarritoItem> buscarPorCompradorYArticulo(UUID compradorId, UUID articuloId);

    void eliminarPorId(UUID id);

    void vaciarCarrito(UUID compradorId);
}