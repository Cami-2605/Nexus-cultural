package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.ArticuloEntity;
import org.springframework.stereotype.Component;

@Component
public class ArticuloMapper {

    // ==========================================
    // Mapeos para Persistencia (PostgreSQL)
    // ==========================================

    public ArticuloEntity toEntity(Articulo domain) {
        if (domain == null) return null;

        return ArticuloEntity.builder()
                .id(domain.getId())
                .titulo(domain.getTitulo())
                .descripcion(domain.getDescripcion())
                .precioMonto(domain.getPrecio() != null ? domain.getPrecio().monto() : null)
                .precioMoneda(domain.getPrecio() != null ? domain.getPrecio().moneda() : "COP")
                .stock(domain.getStock())
                .estado(domain.getEstado() != null ? domain.getEstado().name() : EstadoArticulo.DISPONIBLE.name())
                .grupo(domain.getGrupo() != null ? domain.getGrupo().name() : null)
                .artesanoId(domain.getArtesanoId())
                .imagenes(domain.getImagenes())
                .fechaPublicacion(domain.getFechaPublicacion())
                .build();
    }

    public Articulo toDomain(ArticuloEntity entity) {
        if (entity == null) return null;

        Precio precio = new Precio(
                entity.getPrecioMonto(),
                entity.getPrecioMoneda() != null ? entity.getPrecioMoneda() : "COP"
        );

        EstadoArticulo estado = entity.getEstado() != null
                ? EstadoArticulo.valueOf(entity.getEstado())
                : EstadoArticulo.DISPONIBLE;

        Grupo grupo = entity.getGrupo() != null
                ? Grupo.valueOf(entity.getGrupo())
                : null;

        return Articulo.reconstruir(
                entity.getId(),
                entity.getArtesanoId(),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getStock(),
                estado,
                grupo,
                entity.getImagenes(),
                precio,
                entity.getFechaPublicacion()
        );
    }

    // ==========================================
    // Mapeo para Controllers (REST API)
    // ==========================================

    public ArticuloDetalleResponse toResponse(Articulo domain) {
        if (domain == null) return null;

        return new ArticuloDetalleResponse(
                domain.getId() != null ? domain.getId().toString() : null,
                domain.getArtesanoId() != null ? domain.getArtesanoId().toString() : null,
                domain.getTitulo(),
                domain.getDescripcion(),
                domain.getPrecio() != null && domain.getPrecio().monto() != null
                        ? domain.getPrecio().monto().doubleValue() : 0.0,
                domain.getPrecio() != null ? domain.getPrecio().moneda() : "COP",
                domain.getStock(),
                domain.getEstado() != null ? domain.getEstado().name() : null,
                domain.getGrupo() != null ? domain.getGrupo().name() : null,
                domain.getImagenes(),
                domain.getFechaPublicacion(),
                domain.getPrecioConDescuento() != null && domain.getPrecioConDescuento().monto() != null
                        ? domain.getPrecioConDescuento().monto().doubleValue() : null,
                domain.getDescuento() != null ? domain.getDescuento().porcentaje() : null
        );
    }
}