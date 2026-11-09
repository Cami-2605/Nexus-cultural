package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de aplicación para exploración pública del catálogo artesanal.
 * Expone operaciones de solo lectura separadas de la escritura de {@link ArticuloService}.
 */
@Service
public class CatalogoService {

    private final ArticuloRepository articuloRepository;

    public CatalogoService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    /** Retorna todos los artículos con estado DISPONIBLE. */
    public List<Articulo> listarCatalogo() {
        return articuloRepository.listarDisponibles();
    }

    /** Busca artículos DISPONIBLES cuyo título contenga el query. */
    public List<Articulo> buscarPorTitulo(String query) {
        return articuloRepository.buscarPorTitulo(query);
    }

    /** Retorna artículos DISPONIBLES filtrados por grupo/categoría. */
    public List<Articulo> buscarPorGrupo(Grupo grupo) {
        if (grupo == null) throw new ReglaDominioException("El grupo no puede ser nulo.");
        return articuloRepository.buscarPorGrupo(grupo);
    }

    /** Retorna todos los artículos activos de un artesano específico (excluye eliminados). */
    public List<Articulo> listarPorArtesano(UUID artesanoId) {
        if (artesanoId == null) throw new ReglaDominioException("El ID del artesano es obligatorio.");
        return articuloRepository.listarPorArtesano(artesanoId).stream()
                .filter(a -> a.getEstado() != com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo.ELIMINADO)
                .toList();
    }

    /** Obtiene el detalle de un artículo por su ID. */
    public Articulo obtenerArticulo(UUID id) {
        return articuloRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
    }

    /** Retorna todos los artículos (excluyendo eliminados) para moderación y administración. */
    public List<Articulo> listarTodos() {
        return articuloRepository.listarTodos();
    }
}
