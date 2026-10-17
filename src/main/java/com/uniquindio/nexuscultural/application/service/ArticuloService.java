package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarInventarioRequest;
import com.uniquindio.nexuscultural.application.dto.request.AplicarDescuentoRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearArticuloRequest;
import com.uniquindio.nexuscultural.application.dto.request.ReponerStockRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Descuento;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Servicio de aplicación para la gestión del ciclo de vida de los artículos artesanales.
 */
@Service
public class ArticuloService {

    private final ArticuloRepository articuloRepository;
    private final LogroService logroService;

    public ArticuloService(ArticuloRepository articuloRepository, LogroService logroService) {
        this.articuloRepository = articuloRepository;
        this.logroService = logroService;
    }

    /**
     * Publica un nuevo artículo en el catálogo.
     * Valida moderación (título ≥ 5 chars, descripción ≥ 50 chars) antes de crear.
     * Evalúa logros de gamificación después de guardar.
     */
    public Articulo publicarArticulo(CrearArticuloRequest request) {
        // Moderación: validaciones de contenido antes de crear
        if (request.titulo() == null || request.titulo().trim().length() < 5) {
            throw new ReglaDominioException("El título debe tener al menos 5 caracteres.");
        }
        if (request.descripcion() == null || request.descripcion().trim().length() < 50) {
            throw new ReglaDominioException("La descripción debe tener al menos 50 caracteres.");
        }

        Articulo articulo = Articulo.crear(
                request.artesanoId(),
                request.titulo(),
                request.descripcion(),
                request.stock(),
                request.grupo(),
                request.imagenes(),
                request.precio(),
                LocalDateTime.now()
        );
        Articulo guardado = articuloRepository.guardar(articulo);

        // Gamificación: contar artículos del artesano y evaluar logros
        int totalArticulos = articuloRepository.listarPorArtesano(request.artesanoId()).size();
        logroService.evaluarLogrosPublicacion(request.artesanoId(), totalArticulos);

        return guardado;
    }

    public Articulo actualizarInventario(ActualizarInventarioRequest request) {
        Articulo articulo = articuloRepository.buscarPorId(request.idArticulo())
                .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
        articulo.actualizarInventario(request.idArtesano(), request.cantidad(), request.motivo());
        return articuloRepository.guardar(articulo);
    }

    public Articulo reponerStock(UUID articuloId, ReponerStockRequest request) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
        articulo.reponerStock(request.stock());
        return articuloRepository.guardar(articulo);
    }

    public Articulo inactivarArticulo(UUID articuloId, UUID artesanoId) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
        if (!articulo.getArtesanoId().equals(artesanoId)) {
            throw new ReglaDominioException("Solo el artesano dueño puede inactivar el artículo.");
        }
        articulo.inactivar();
        return articuloRepository.guardar(articulo);
    }

    public Articulo reactivarArticulo(UUID articuloId, UUID artesanoId) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
        if (!articulo.getArtesanoId().equals(artesanoId)) {
            throw new ReglaDominioException("Solo el artesano dueño puede reactivar el artículo.");
        }
        articulo.reactivar();
        return articuloRepository.guardar(articulo);
    }

    /**
     * Aplica un descuento porcentual a un artículo.
     * Solo el artesano dueño puede aplicar el descuento.
     */
    public Articulo aplicarDescuento(UUID articuloId, AplicarDescuentoRequest request) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
        if (!articulo.getArtesanoId().equals(request.artesanoId())) {
            throw new ReglaDominioException("Solo el artesano dueño puede aplicar un descuento.");
        }
        Descuento descuento = new Descuento(request.porcentaje(), request.motivo());
        articulo.aplicarDescuento(descuento);
        return articuloRepository.guardar(articulo);
    }

    /**
     * Remueve el descuento activo de un artículo.
     * Solo el artesano dueño puede remover el descuento.
     */
    public Articulo removerDescuento(UUID articuloId, UUID artesanoId) {
        Articulo articulo = articuloRepository.buscarPorId(articuloId)
                .orElseThrow(() -> new ReglaDominioException("Artículo no encontrado."));
        if (!articulo.getArtesanoId().equals(artesanoId)) {
            throw new ReglaDominioException("Solo el artesano dueño puede remover el descuento.");
        }
        articulo.removerDescuento();
        return articuloRepository.guardar(articulo);
    }
}
