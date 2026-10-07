package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarInventarioRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;

public class ActualizarInventarioUseCase {

    private final ArticuloRepository articuloRepository;

    public ActualizarInventarioUseCase(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public void ejecutar(ActualizarInventarioRequest request) {
        // 1. Buscar el artículo en el repositorio
        Articulo articulo = articuloRepository.buscarPorId(request.idArticulo())
                .orElseThrow(() -> new IllegalArgumentException("El artículo no existe."));

        // 2. Delegar la regla de negocio al Agregado (Domain Rule)
        articulo.actualizarInventario(request.idArtesano(), request.cantidad(), request.motivo());

        // 3. Persistir el estado actualizado en el repositorio
        articuloRepository.guardar(articulo);
    }
}
