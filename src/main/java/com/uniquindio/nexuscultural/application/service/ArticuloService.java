package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarInventarioRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearArticuloRequest;
import com.uniquindio.nexuscultural.application.dto.request.ReponerStockRequest;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ArticuloService {

    private final ArticuloRepository articuloRepository;

    public ArticuloService(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    public Articulo publicarArticulo(CrearArticuloRequest request) {
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
        return articuloRepository.guardar(articulo);
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
}
