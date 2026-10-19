package com.uniquindio.nexuscultural.infraestructure.persistence.repository;

import com.uniquindio.nexuscultural.infraestructure.persistence.entity.CarritoItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCarritoItemRepository extends JpaRepository<CarritoItemEntity, UUID> {
    List<CarritoItemEntity> findByCompradorId(UUID compradorId);
    Optional<CarritoItemEntity> findByCompradorIdAndArticuloId(UUID compradorId, UUID articuloId);
    void deleteByCompradorId(UUID compradorId);
}