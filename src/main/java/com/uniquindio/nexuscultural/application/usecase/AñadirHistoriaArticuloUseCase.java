package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;

import java.util.UUID;

public class AñadirHistoriaArticuloUseCase {

    private final ArticuloRepository articuloRepository;

    public AñadirHistoriaArticuloUseCase(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public Articulo ejecutar(UUID articuloId) {
        // Buscar el artículo existente
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ReglaDominioException("No existe un artículo con el ID proporcionado"));

        // Validar si el artículo ya tiene historia/descripción
        if (articulo.getDescripcion() != null && !articulo.getDescripcion().trim().isEmpty()) {
            throw new ReglaDominioException("El artículo ya tiene una historia/descripción asignada");
        }

        // Si no tiene historia, se solicita al artesano que la agregue
        throw new ReglaDominioException("El artículo no tiene historia. Por favor, agregue la descripción/historia del artículo.");
    }

    public Articulo ejecutar(UUID articuloId, String historia) {
        // Validar que la historia no sea nula o vacía
        if (historia == null || historia.trim().isEmpty()) {
            throw new ReglaDominioException("La historia del artículo es obligatoria");
        }

        // Buscar el artículo existente
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ReglaDominioException("No existe un artículo con el ID proporcionado"));

        // Validar si el artículo ya tiene historia/descripción
        if (articulo.getDescripcion() != null && !articulo.getDescripcion().trim().isEmpty()) {
            throw new ReglaDominioException("El artículo ya tiene una historia/descripción asignada");
        }

        // Añadir la historia al artículo
        articulo.actualizarDescripcion(historia);

        // Guardar el artículo actualizado
        return articuloRepository.guardar(articulo);
    }
}
