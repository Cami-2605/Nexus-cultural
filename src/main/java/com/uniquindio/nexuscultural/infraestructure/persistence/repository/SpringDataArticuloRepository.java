package com.uniquindio.nexuscultural.infraestructure.persistence.repository;

import com.uniquindio.nexuscultural.infraestructure.persistence.entity.ArticuloEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpringDataArticuloRepository extends JpaRepository<ArticuloEntity, UUID> {

    List<ArticuloEntity> findByEstado(String estado);

    List<ArticuloEntity> findByEstadoAndTituloContainingIgnoreCase(String estado, String query);

    List<ArticuloEntity> findByEstadoAndGrupo(String estado, String grupo);

    List<ArticuloEntity> findByArtesanoId(UUID artesanoId);
}