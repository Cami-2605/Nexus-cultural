package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Pedido {

    private static final int PLAZO_DEVOLUCION_DIAS = 5; // 5 días calendario desde la entrega (Ley 1480 de 2011)

    private final UUID id;
    private final UUID compradorId;
    private final UUID articuloId;
    private final int cantidad;
    private final Precio total;
    private final LocalDateTime fechaCreacion;

    private EstadoPedido estado;
    private LocalDateTime fechaEntrega;
    private String metodoPago;
    private String referenciaTransaccion;
    private Devolucion devolucion; /*Solo existe para que no el pedido tenga una instancia de devolución
    en caso de que no se haga, siempre será null*/

    private Pedido(UUID id, UUID compradorId, UUID articuloId, int cantidad,
                   Precio total, LocalDateTime fechaCreacion) {
        this.id = id;
        this.compradorId = compradorId;
        this.articuloId = articuloId;
        this.cantidad = cantidad;
        this.total = total;
        this.fechaCreacion = fechaCreacion;
        this.estado = EstadoPedido.PENDIENTE;
    }

    public static Pedido crear(UUID compradorId, UUID articuloId, int cantidad,
                               Precio precioUnitario, LocalDateTime ahora) {
        if (compradorId == null || articuloId == null) {
            throw new ReglaDominioException("El pedido necesita comprador y artículo.");
        }
        if (cantidad <= 0) {
            throw new ReglaDominioException("La cantidad debe ser mayor a cero.");
        }
        if (precioUnitario == null) {
            throw new ReglaDominioException("El pedido necesita el precio del artículo.");
        }
        Precio total = new Precio(
                precioUnitario.monto().multiply(BigDecimal.valueOf(cantidad)),
                precioUnitario.moneda());
        return new Pedido(UUID.randomUUID(), compradorId, articuloId, cantidad, total, ahora);
    }

    public void confirmar() {
        verificarEstado(EstadoPedido.PENDIENTE);
        estado = EstadoPedido.CONFIRMADO;
    }

    /**
     * Cancela un pedido. Solo el comprador puede cancelarlo y únicamente
     * cuando está en estado PENDIENTE (antes del pago/confirmación).
     * Si el pedido ya fue confirmado o está en camino, debe gestionarse como devolución.
     */
    public void cancelar(UUID solicitanteId) {
        if (!compradorId.equals(solicitanteId)) {
            throw new ReglaDominioException("Solo el comprador del pedido puede cancelarlo.");
        }
        if (estado != EstadoPedido.PENDIENTE) {
            throw new ReglaDominioException(
                    "Solo se puede cancelar un pedido pendiente. Estado actual: " + estado);
        }
        estado = EstadoPedido.CANCELADO;
    }

    /**
     * Registra el pago de un pedido pendiente. Todas las validaciones ocurren
     * antes de modificar nada, así un pago rechazado no deja el pedido a medias.
     */
    public void procesarPago(Precio monto, String metodoPago, String referenciaTransaccion) {
        verificarEstado(EstadoPedido.PENDIENTE);
        // compareTo y no equals: BigDecimal distingue 30000 de 30000.00 con equals
        if (monto == null
                || !monto.moneda().equals(total.moneda())
                || monto.monto().compareTo(total.monto()) != 0) {
            throw new ReglaDominioException("El monto del pago no coincide con el total del pedido.");
        }
        if (metodoPago == null || metodoPago.isBlank()) {
            throw new ReglaDominioException("El método de pago es obligatorio.");
        }
        if (referenciaTransaccion == null || referenciaTransaccion.isBlank()) {
            throw new ReglaDominioException("La referencia de transacción es obligatoria.");
        }
        this.metodoPago = metodoPago;
        this.referenciaTransaccion = referenciaTransaccion;
        this.estado = EstadoPedido.CONFIRMADO;
    }

    public void enviar() {
        verificarEstado(EstadoPedido.CONFIRMADO);
        estado = EstadoPedido.EN_CAMINO;
    }

    public void marcarEntregado(LocalDateTime ahora) {
        verificarEstado(EstadoPedido.EN_CAMINO);
        estado = EstadoPedido.ENTREGADO;
        fechaEntrega = ahora;
    }

    /**
     * Invariante 1 del agregado: un pedido no puede tener más de una
     * devolución activa al mismo tiempo.
     */
    public Devolucion solicitarDevolucion(UUID solicitanteId, String motivo, LocalDateTime ahora) {
        if (!compradorId.equals(solicitanteId)) {
            throw new ReglaDominioException("Solo el comprador del pedido puede solicitar la devolución.");
        }
        if (estado != EstadoPedido.ENTREGADO) {
            throw new ReglaDominioException("Solo se puede solicitar la devolución de un pedido entregado.");
        }
        if (devolucion != null) {
            throw new ReglaDominioException("Este pedido ya tiene una devolución en proceso.");
        }
        if (ahora.isAfter(fechaEntrega.plusDays(PLAZO_DEVOLUCION_DIAS))) {
            throw new ReglaDominioException("El plazo de devolución (" + PLAZO_DEVOLUCION_DIAS + " días) ya venció.");
        }
        devolucion = Devolucion.crear(id, motivo, ahora);
        estado = EstadoPedido.EN_DEVOLUCION;
        return devolucion;
    }

    /**
     * Invariante 2 del agregado: la Devolucion nunca se resuelve directamente;
     * siempre a través de la raíz, para que el estado del Pedido quede consistente
     * con el de su Devolucion.
     */
    public void resolverDevolucion(boolean aprobar) {
        if (devolucion == null) {
            throw new ReglaDominioException("Este pedido no tiene una devolución en proceso.");
        }
        if (aprobar) {
            devolucion.aprobar();
            estado = EstadoPedido.DEVUELTO;
        } else {
            devolucion.rechazar();
            estado = EstadoPedido.ENTREGADO; // vuelve al estado anterior a la solicitud
        }
    }

    private void verificarEstado(EstadoPedido estadoEsperado) {
        if (estado != estadoEsperado) {
            throw new ReglaDominioException("No fue posible proceder, el sistema necesita que el pedido esté en estado "
                    + estadoEsperado + " y actualmente se encuentra en estado " + estado + ".");
        }
    }

    public UUID getId() { return id; }
    public UUID getCompradorId() { return compradorId; }
    public UUID getArticuloId() { return articuloId; }
    public int getCantidad() { return cantidad; }
    public Precio getTotal() { return total; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public EstadoPedido getEstado() { return estado; }
    public String getMetodoPago() { return metodoPago; }
    public String getReferenciaTransaccion() { return referenciaTransaccion; }
    public Devolucion getDevolucion() { return devolucion; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pedido otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
