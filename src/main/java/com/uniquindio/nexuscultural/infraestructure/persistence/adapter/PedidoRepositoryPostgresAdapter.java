package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.PedidoEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.repository.SpringDataPedidoRepository;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.PedidoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@Primary
@RequiredArgsConstructor
public class PedidoRepositoryPostgresAdapter implements PedidoRepository {

    private final SpringDataPedidoRepository springDataPedidoRepository;
    private final PedidoMapper pedidoMapper;

    @Override
    public void guardar(Pedido pedido) {
        PedidoEntity entity = pedidoMapper.toEntity(pedido);
        springDataPedidoRepository.save(entity);
    }

    @Override
    public Optional<Pedido> obtenerPorId(UUID id) {
        return springDataPedidoRepository.findById(id)
                .map(pedidoMapper::toDomain);
    }

    @Override
    public List<Pedido> buscarPorCompradorId(UUID compradorId) {
        return springDataPedidoRepository.findByCompradorId(compradorId)
                .stream()
                .map(pedidoMapper::toDomain)
                .collect(Collectors.toList());
    }
}