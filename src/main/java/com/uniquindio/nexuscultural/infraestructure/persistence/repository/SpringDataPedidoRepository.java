package com.uniquindio.nexuscultural.infraestructure.persistence.repository;

import com.uniquindio.nexuscultural.infraestructure.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataPedidoRepository extends JpaRepository<PedidoEntity, UUID> {
    List<PedidoEntity> findByCompradorId(UUID compradorId);
}