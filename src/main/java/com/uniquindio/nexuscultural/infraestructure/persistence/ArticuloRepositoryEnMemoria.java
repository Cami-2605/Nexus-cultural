package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación en memoria del repositorio de artículos.
 */
public class ArticuloRepositoryEnMemoria implements ArticuloRepository {

    private final Map<UUID, Articulo> articulos = new HashMap<>();

    @Override
    public Articulo guardar(Articulo articulo) {
        articulos.put(articulo.getId(), articulo);
        return articulo;
    }

    @Override
    public Optional<Articulo> buscarPorId(UUID id) {
        return Optional.ofNullable(articulos.get(id));
    }

    @Override
    public void eliminar(UUID id) {
        articulos.remove(id);
    }

    @Override
    public List<Articulo> listarDisponibles() {
        return articulos.values().stream()
                .filter(a -> a.getEstado() == EstadoArticulo.DISPONIBLE)
                .toList();
    }

    @Override
    public List<Articulo> buscarPorTitulo(String query) {
        if (query == null || query.isBlank()) return listarDisponibles();
        String q = query.toLowerCase();
        return articulos.values().stream()
                .filter(a -> a.getEstado() == EstadoArticulo.DISPONIBLE)
                .filter(a -> a.getTitulo().toLowerCase().contains(q))
                .toList();
    }

    @Override
    public List<Articulo> buscarPorGrupo(Grupo grupo) {
        return articulos.values().stream()
                .filter(a -> a.getEstado() == EstadoArticulo.DISPONIBLE)
                .filter(a -> a.getGrupo() == grupo)
                .toList();
    }

    @Override
    public List<Articulo> listarPorArtesano(UUID artesanoId) {
        return articulos.values().stream()
                .filter(a -> a.getArtesanoId().equals(artesanoId))
                .toList();
    }
}
