package com.uniquindio.nexuscultural.infraestructure.persistence.repository;

import com.uniquindio.nexuscultural.infraestructure.persistence.entity.CarritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCarritoRepository extends JpaRepository<CarritoEntity, UUID> {
    Optional<CarritoEntity> findByCompradorId(UUID compradorId);
}