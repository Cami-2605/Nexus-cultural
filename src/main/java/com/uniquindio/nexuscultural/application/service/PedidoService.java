package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.DevolverProductoRequest;
import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ArticuloRepository articuloRepository;

    public PedidoService(PedidoRepository pedidoRepository,
                         ArticuloRepository articuloRepository) {
        this.pedidoRepository = pedidoRepository;
        this.articuloRepository = articuloRepository;
    }

    /**
     * Crea un pedido y reduce el stock del artículo de forma atómica.
     * Si el artículo no tiene stock suficiente, Articulo.reducirStock() lanza la excepción
     * antes de persistir nada, dejando el sistema consistente.
     */
    public Pedido crearPedido(UUID compradorId, UUID articuloId, int cantidad) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));

        Pedido pedido = Pedido.crear(compradorId, articuloId, cantidad,
                articulo.getPrecio(), LocalDateTime.now());

        // Reducir stock ANTES de guardar: si falla, el pedido no queda persistido
        articulo.reducirStock(cantidad);

        articuloRepository.guardar(articulo);
        pedidoRepository.guardar(pedido);
        return pedido;
    }

    public Pedido procesarPago(ProcesarPagoRequest request) {
        Pedido pedido = pedidoRepository.obtenerPorId(request.idPedido())
                .orElseThrow(() -> new ReglaDominioException("Pedido no encontrado."));
        pedido.procesarPago(request.monto(), request.metodoPago(), request.referenciaTransaccion());
        pedidoRepository.guardar(pedido);
        return pedido;
    }

    /**
     * Cancela el pedido y devuelve el stock al artículo.
     */
    public Pedido cancelarPedido(UUID pedidoId, UUID solicitanteId) {
        Pedido pedido = pedidoRepository.obtenerPorId(pedidoId)
                .orElseThrow(() -> new ReglaDominioException("Pedido no encontrado."));

        pedido.cancelar(solicitanteId);

        Articulo articulo = articuloRepository.buscarPorId(pedido.getArticuloId())
                .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
        articulo.reponerStock(pedido.getCantidad());

        articuloRepository.guardar(articulo);
        pedidoRepository.guardar(pedido);
        return pedido;
    }

    public Devolucion solicitarDevolucion(DevolverProductoRequest request) {
        Pedido pedido = pedidoRepository.obtenerPorId(request.idPedido())
                .orElseThrow(() -> new ReglaDominioException("Pedido no encontrado."));

        Devolucion devolucion = pedido.solicitarDevolucion(
                request.idComprador(), request.motivo(), LocalDateTime.now());
        pedidoRepository.guardar(pedido);
        return devolucion;
    }

    /**
     * Si la devolución se aprueba, el stock vuelve al artículo.
     */
    public Pedido resolverDevolucion(UUID pedidoId, boolean aprobar) {
        Pedido pedido = pedidoRepository.obtenerPorId(pedidoId)
                .orElseThrow(() -> new ReglaDominioException("Pedido no encontrado."));

        pedido.resolverDevolucion(aprobar);

        if (aprobar) {
            Articulo articulo = articuloRepository.buscarPorId(pedido.getArticuloId())
                    .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
            articulo.reponerStock(pedido.getCantidad());
            articuloRepository.guardar(articulo);
        }

        pedidoRepository.guardar(pedido);
        return pedido;
    }
}
