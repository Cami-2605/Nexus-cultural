package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.Articulo;

import java.util.Optional;
import java.util.UUID;

public interface ArticuloRepository {

    Articulo guardar(Articulo articulo);

    Optional<Articulo> buscarPorId(UUID id);

    void eliminar(UUID id);
}
