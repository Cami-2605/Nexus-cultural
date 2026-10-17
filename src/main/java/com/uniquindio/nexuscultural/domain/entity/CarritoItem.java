package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

import java.util.UUID;

/**
 * Entidad que representa un ítem dentro del carrito de compras de un comprador.
 */
public class CarritoItem {

    private final UUID id;
    private final UUID compradorId;
    private final UUID articuloId;
    private int cantidad;

    /**
     * Constructor de creación: genera un nuevo ID y valida las reglas de negocio.
     */
    public CarritoItem(UUID compradorId, UUID articuloId, int cantidad) {
        if (compradorId == null) throw new ReglaDominioException("El comprador es obligatorio.");
        if (articuloId == null) throw new ReglaDominioException("El artículo es obligatorio.");
        if (cantidad <= 0) throw new ReglaDominioException("La cantidad debe ser mayor a cero.");
        this.id = UUID.randomUUID();
        this.compradorId = compradorId;
        this.articuloId = articuloId;
        this.cantidad = cantidad;
    }

    /** Constructor privado para reconstrucción desde persistencia. */
    private CarritoItem(UUID id, UUID compradorId, UUID articuloId, int cantidad) {
        this.id = id;
        this.compradorId = compradorId;
        this.articuloId = articuloId;
        this.cantidad = cantidad;
    }

    /**
     * Factory method para reconstruir un ítem ya persistido (no revalida reglas de creación).
     * Solo debe usarla la capa de infraestructura.
     */
    public static CarritoItem reconstruir(UUID id, UUID compradorId, UUID articuloId, int cantidad) {
        return new CarritoItem(id, compradorId, articuloId, cantidad);
    }

    /** Actualiza la cantidad del ítem. La cantidad debe ser mayor a cero. */
    public void actualizarCantidad(int nuevaCantidad) {
        if (nuevaCantidad <= 0) throw new ReglaDominioException("La cantidad debe ser mayor a cero.");
        this.cantidad = nuevaCantidad;
    }

    public UUID getId() { return id; }
    public UUID getCompradorId() { return compradorId; }
    public UUID getArticuloId() { return articuloId; }
    public int getCantidad() { return cantidad; }
}
