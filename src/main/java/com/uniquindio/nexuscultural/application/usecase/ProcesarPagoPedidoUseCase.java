package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;

public class ProcesarPagoPedidoUseCase {

    private final PedidoRepository pedidoRepository;

    public ProcesarPagoPedidoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(ProcesarPagoRequest request) {
        // 1. Buscar el pedido en el repositorio
        Pedido pedido = pedidoRepository.obtenerPorId(request.idPedido())
                .orElseThrow(() -> new IllegalArgumentException("El pedido no existe."));

        // 2. Validar que el comprador que realiza el pago sea el dueño del pedido
        if (!pedido.getCompradorId().equals(request.idComprador())) {
            throw new IllegalStateException("Solo el comprador original puede procesar el pago de este pedido.");
        }

        // 3. Delegar al agregado: valida monto, método de pago y referencia
        pedido.procesarPago(request.monto(), request.metodoPago(), request.referenciaTransaccion());

        // 4. Persistir el estado actualizado en el repositorio
        pedidoRepository.guardar(pedido);
    }
}
