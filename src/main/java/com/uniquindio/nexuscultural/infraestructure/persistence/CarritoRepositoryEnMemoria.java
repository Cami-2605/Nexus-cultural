package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CarritoRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del repositorio del carrito de compras.
 */
public class CarritoRepositoryEnMemoria implements CarritoRepository {

    private final Map<UUID, CarritoItem> items = new HashMap<>();

    @Override
    public CarritoItem guardar(CarritoItem item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<CarritoItem> buscarPorComprador(UUID compradorId) {
        return items.values().stream()
                .filter(i -> i.getCompradorId().equals(compradorId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CarritoItem> buscarPorId(UUID itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public void eliminar(UUID itemId) {
        items.remove(itemId);
    }

    @Override
    public void eliminarPorComprador(UUID compradorId) {
        items.entrySet().removeIf(e -> e.getValue().getCompradorId().equals(compradorId));
    }

    @Override
    public CarritoItem actualizarCantidad(UUID itemId, int nuevaCantidad) {
        CarritoItem item = items.get(itemId);
        if (item == null) throw new ReglaDominioException("Ítem del carrito no encontrado.");
        item.actualizarCantidad(nuevaCantidad);
        return item;
    }
}
