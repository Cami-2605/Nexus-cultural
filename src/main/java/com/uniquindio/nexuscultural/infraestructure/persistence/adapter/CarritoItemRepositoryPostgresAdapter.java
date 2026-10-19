package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import com.uniquindio.nexuscultural.domain.repository.CarritoItemRepository;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.CarritoItemEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.repository.SpringDataCarritoItemRepository;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.CarritoItemMapper;
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
public class CarritoItemRepositoryPostgresAdapter implements CarritoItemRepository {

    private final SpringDataCarritoItemRepository springDataCarritoItemRepository;
    private final CarritoItemMapper carritoItemMapper;

    @Override
    public CarritoItem guardar(CarritoItem item) {
        CarritoItemEntity entity = carritoItemMapper.toEntity(item);
        CarritoItemEntity saved = springDataCarritoItemRepository.save(entity);
        return carritoItemMapper.toDomain(saved);
    }

    @Override
    public Optional<CarritoItem> buscarPorId(UUID id) {
        return springDataCarritoItemRepository.findById(id)
                .map(carritoItemMapper::toDomain);
    }

    @Override
    public List<CarritoItem> buscarPorCompradorId(UUID compradorId) {
        return springDataCarritoItemRepository.findByCompradorId(compradorId)
                .stream()
                .map(carritoItemMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CarritoItem> buscarPorCompradorYArticulo(UUID compradorId, UUID articuloId) {
        return springDataCarritoItemRepository.findByCompradorIdAndArticuloId(compradorId, articuloId)
                .map(carritoItemMapper::toDomain);
    }

    @Override
    public void eliminarPorId(UUID id) {
        springDataCarritoItemRepository.deleteById(id);
    }

    @Override
    public void vaciarCarrito(UUID compradorId) {
        springDataCarritoItemRepository.deleteByCompradorId(compradorId);
    }
}