package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.DevolverProductoRequest;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;

import java.time.LocalDateTime;

public class DevolverProductoUseCase {

    private final PedidoRepository pedidoRepository;

    public DevolverProductoUseCase(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public void ejecutar(DevolverProductoRequest request) {
        // 1. Buscar el pedido en el repositorio
        Pedido pedido = pedidoRepository.obtenerPorId(request.idPedido())
                .orElseThrow(() -> new IllegalArgumentException("El pedido no existe."));

        // 2. Delegar la regla de negocio al Agregado (Domain Rule)
        pedido.solicitarDevolucion(request.idComprador(), request.motivo(), LocalDateTime.now());

        // 3. Persistir el estado actualizado en el repositorio
        pedidoRepository.guardar(pedido);
    }
}
