package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
}
