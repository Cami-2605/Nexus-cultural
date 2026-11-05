package com.uniquindio.nexuscultural.infraestructure.persistence.repository;

import com.uniquindio.nexuscultural.infraestructure.persistence.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataPedidoRepository extends JpaRepository<PedidoEntity, UUID> {
    List<PedidoEntity> findByCompradorId(UUID compradorId);

    @Query("SELECT p FROM PedidoEntity p WHERE p.articuloId IN (SELECT a.id FROM ArticuloEntity a WHERE a.artesanoId = :artesanoId)")
    List<PedidoEntity> findByArtesanoId(@Param("artesanoId") UUID artesanoId);
}