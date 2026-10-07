package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.Pedido;

import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository {
    Optional<Pedido> obtenerPorId(UUID id);
    void guardar(Pedido pedido);
}