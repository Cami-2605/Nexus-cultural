package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Articulo {

    private static final int MINIMO_IMAGENES = 2;

    private final UUID id;
    private final UUID artesanoId;          // referencia por ID, no por objeto
    private final Grupo grupo;
    private final LocalDateTime fechaPublicacion;
    private final List<String> imagenes;    // el dominio solo conoce referencias (URLs/keys)

    private String titulo;
    private String descripcion;
    private int stock;
    private EstadoArticulo estado;
    private Precio precio;

    private Articulo(UUID id, UUID artesanoId, String titulo, String descripcion,
                     int stock, EstadoArticulo estado, Grupo grupo, List<String> imagenes,
                     Precio precio, LocalDateTime fechaPublicacion) {
        this.id = id;
        this.artesanoId = artesanoId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.stock = stock;
        this.estado = estado;
        this.grupo = grupo;
        this.imagenes = new ArrayList<>(imagenes);
        this.precio = precio;
        this.fechaPublicacion = fechaPublicacion;
    }

    /** Única puerta de entrada para crear un artículo NUEVO: aquí se validan las reglas. */
    public static Articulo crear(UUID artesanoId, String titulo, String descripcion,
                                 int stockInicial, Grupo grupo, List<String> imagenes,
                                 Precio precio, LocalDateTime ahora) {
        if (artesanoId == null) {
            throw new ReglaDominioException("El artículo debe pertenecer a un artesano.");
        }
        if (titulo == null || titulo.isBlank()) {
            throw new ReglaDominioException("El artículo debe tener un título.");
        }
        if (grupo == null) {
            throw new ReglaDominioException("El artículo debe pertenecer a un grupo.");
        }
        if (precio == null) {
            throw new ReglaDominioException("El artículo debe tener un precio.");
        }
        if (stockInicial < 1) {
            throw new ReglaDominioException("Un artículo nuevo debe tener al menos 1 unidad en stock.");
        }
        if (imagenes == null || imagenes.size() < MINIMO_IMAGENES) {
            throw new ReglaDominioException(
                    "Un artículo debe tener mínimo " + MINIMO_IMAGENES + " imágenes.");
        }
        return new Articulo(UUID.randomUUID(), artesanoId, titulo, descripcion,
                stockInicial, EstadoArticulo.DISPONIBLE, grupo, imagenes, precio, ahora);
    }

    /**
     * Reconstruye un artículo YA EXISTENTE (por ejemplo, al leerlo de la base de datos
     * o del repositorio en memoria). No vuelve a validar las reglas de creación.
     * Solo debe usarla infrastructure.
     */
    public static Articulo reconstruir(UUID id, UUID artesanoId, String titulo, String descripcion,
                                       int stock, EstadoArticulo estado, Grupo grupo,
                                       List<String> imagenes, Precio precio,
                                       LocalDateTime fechaPublicacion) {
        return new Articulo(id, artesanoId, titulo, descripcion, stock, estado,
                grupo, imagenes, precio, fechaPublicacion);
    }

    /** Se llama al confirmar un pedido. Si llega a 0 el artículo queda AGOTADO. */
    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaDominioException("La cantidad debe ser mayor a cero.");
        }
        if (estado != EstadoArticulo.DISPONIBLE) {
            throw new ReglaDominioException("El artículo no está disponible para la venta.");
        }
        if (cantidad > stock) {
            throw new ReglaDominioException("Stock insuficiente: el stock no puede ser negativo.");
        }
        stock -= cantidad;
        if (stock == 0) {
            estado = EstadoArticulo.AGOTADO;
        }
    }

    public void reponerStock(int cantidad) {
        if (cantidad <= 0) {
            throw new ReglaDominioException("La cantidad a reponer debe ser mayor a cero.");
        }
        if (estado == EstadoArticulo.ELIMINADO) {
            throw new ReglaDominioException("No se puede reponer stock de un artículo eliminado.");
        }
        stock += cantidad;
        // Reponer stock no reactiva un artículo que el artesano sacó de venta a propósito.
        if (estado == EstadoArticulo.AGOTADO) {
            estado = EstadoArticulo.DISPONIBLE;
        }
    }

    /** El artesano lo saca de venta temporalmente. A diferencia de eliminar(), es reversible. */
    public void inactivar() {
        if (estado == EstadoArticulo.ELIMINADO) {
            throw new ReglaDominioException("No se puede inactivar un artículo ya eliminado.");
        }
        if (estado == EstadoArticulo.INACTIVO) {
            throw new ReglaDominioException("El artículo ya está inactivo.");
        }
        estado = EstadoArticulo.INACTIVO;
    }

    /** Vuelve a poner el artículo en venta. Su disponibilidad depende del stock que tenga. */
    public void reactivar() {
        if (estado != EstadoArticulo.INACTIVO) {
            throw new ReglaDominioException("Solo se puede reactivar un artículo inactivo.");
        }
        estado = (stock > 0) ? EstadoArticulo.DISPONIBLE : EstadoArticulo.AGOTADO;
    }

    /**
     * Soft delete. El dominio no consulta pedidos (no conoce la BD):
     * el caso de uso averigua si hay pedidos activos y se lo informa aquí.
     */
    public void eliminar(boolean tienePedidosActivosOSujetosADevolucion) {
        if (estado == EstadoArticulo.ELIMINADO) {
            throw new ReglaDominioException("El artículo ya fue eliminado.");
        }
        if (tienePedidosActivosOSujetosADevolucion) {
            throw new ReglaDominioException(
                    "No se puede eliminar un artículo con pedidos activos o sujetos a devolución.");
        }
        estado = EstadoArticulo.ELIMINADO;
    }

    /**
     * Ajuste de inventario (ej. conteo físico, corrección de stock). A diferencia de
     * reducirStock/reponerStock (que mueven el stock en delta al vender/reponer),
     * este método fija la cantidad EXACTA y exige un motivo para trazabilidad.
     */
    public void actualizarInventario(UUID solicitanteId, int nuevaCantidad, String motivo) {
        if (!artesanoId.equals(solicitanteId)) {
            throw new ReglaDominioException("Solo el artesano dueño del artículo puede actualizar su inventario.");
        }
        if (estado == EstadoArticulo.ELIMINADO) {
            throw new ReglaDominioException("No se puede actualizar el inventario de un artículo eliminado.");
        }
        if (nuevaCantidad < 0) {
            throw new ReglaDominioException("La cantidad no puede ser negativa.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaDominioException("El motivo de la actualización de inventario es obligatorio.");
        }
        this.stock = nuevaCantidad;
        // No reactiva un artículo que el artesano puso INACTIVO a propósito.
        if (estado != EstadoArticulo.INACTIVO) {
            estado = (nuevaCantidad > 0) ? EstadoArticulo.DISPONIBLE : EstadoArticulo.AGOTADO;
        }
    }

    public UUID getId() { return id; }
    public UUID getArtesanoId() { return artesanoId; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public int getStock() { return stock; }
    public EstadoArticulo getEstado() { return estado; }
    public Grupo getGrupo() { return grupo; }
    public Precio getPrecio() { return precio; }
    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public List<String> getImagenes() { return Collections.unmodifiableList(imagenes); }

    public void actualizarDescripcion(String nuevaDescripcion) {
        if (nuevaDescripcion == null || nuevaDescripcion.isBlank()) {
            throw new ReglaDominioException("La descripción no puede estar vacía.");
        }
        this.descripcion = nuevaDescripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Articulo otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
