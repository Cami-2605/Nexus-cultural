package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.Pedido;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PedidoRepository {
    Optional<Pedido> obtenerPorId(UUID id);
    void guardar(Pedido pedido);
    List<Pedido> buscarPorCompradorId(UUID compradorId);
    List<Pedido> buscarPorArtesanoId(UUID artesanoId);
    List<Pedido> listarDevoluciones();
    Optional<Pedido> buscarPorReferenciaTransaccion(String referenciaTransaccion);
    List<Pedido> listarTodos();
}