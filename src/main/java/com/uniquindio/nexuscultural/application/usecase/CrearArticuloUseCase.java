package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CrearArticuloUseCase {

    private final ArticuloRepository articuloRepository;

    public CrearArticuloUseCase(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public Articulo ejecutar(UUID artesanoId, String titulo, int stock, String descripcion, Grupo grupo) {
        // Crear el artículo con la historia/descripción
        Articulo articulo = Articulo.crear(
                artesanoId,
                titulo,
                descripcion,
                stock,
                grupo,
                List.of(), // Lista vacía para evitar NullPointerException (Temporal)
                null,      // Precio temporal
                LocalDateTime.now()
        );

        // Guardar el artículo
        return articuloRepository.guardar(articulo);
    }
}
