package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PedidoRepositoryEnMemoria implements PedidoRepository {

    private final Map<UUID, Pedido> pedidos = new HashMap<>();

    @Override
    public Optional<Pedido> obtenerPorId(UUID id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    @Override
    public void guardar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }
}
