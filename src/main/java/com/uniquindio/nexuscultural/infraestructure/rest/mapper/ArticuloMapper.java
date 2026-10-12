package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import org.springframework.stereotype.Component;

@Component
public class ArticuloMapper {

    public ArticuloDetalleResponse toResponse(Articulo articulo) {
        if (articulo == null) {
            return null;
        }

        return new ArticuloDetalleResponse(
                articulo.getId() != null ? articulo.getId().toString() : null,
                articulo.getArtesanoId() != null ? articulo.getArtesanoId().toString() : null,
                articulo.getTitulo(),
                articulo.getDescripcion(),
                articulo.getPrecio() != null ? articulo.getPrecio().monto().doubleValue() : 0.0,
                articulo.getPrecio() != null ? "COP" : "COP",
                articulo.getStock(),
                articulo.getEstado() != null ? articulo.getEstado().name() : null,
                articulo.getGrupo() != null ? articulo.getGrupo().name() : null,
                articulo.getImagenes(),
                articulo.getFechaPublicacion()
        );
    }
}