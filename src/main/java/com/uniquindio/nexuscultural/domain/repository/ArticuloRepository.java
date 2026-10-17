package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de acceso al repositorio de artículos.
 */
public interface ArticuloRepository {

    Articulo guardar(Articulo articulo);

    Optional<Articulo> buscarPorId(UUID id);

    void eliminar(UUID id);

    /** Retorna todos los artículos con estado DISPONIBLE. */
    List<Articulo> listarDisponibles();

    /** Busca artículos DISPONIBLES cuyo título contenga el query (case-insensitive). */
    List<Articulo> buscarPorTitulo(String query);

    /** Retorna todos los artículos DISPONIBLES que pertenezcan al grupo indicado. */
    List<Articulo> buscarPorGrupo(Grupo grupo);

    /** Retorna todos los artículos del artesano, sin filtrar por estado. */
    List<Articulo> listarPorArtesano(UUID artesanoId);
}
