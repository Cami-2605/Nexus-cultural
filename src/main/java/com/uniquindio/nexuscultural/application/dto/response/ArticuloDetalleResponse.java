package com.uniquindio.nexuscultural.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta con el detalle completo de un artículo del catálogo.
 * Los campos {@code precioConDescuento} y {@code porcentajeDescuento} son {@code null}
 * cuando no hay descuento activo.
 */
public record ArticuloDetalleResponse(
        String id,
        String artesanoId,
        String titulo,
        String descripcion,
        double montoPrecio,
        String moneda,
        int stock,
        String estado,
        String grupo,
        List<String> imagenes,
        LocalDateTime fechaPublicacion,
        Double precioConDescuento,
        Integer porcentajeDescuento,
        String departamento,
        String municipio
) {
    public ArticuloDetalleResponse(
            String id,
            String artesanoId,
            String titulo,
            String descripcion,
            double montoPrecio,
            String moneda,
            int stock,
            String estado,
            String grupo,
            List<String> imagenes,
            LocalDateTime fechaPublicacion,
            Double precioConDescuento,
            Integer porcentajeDescuento
    ) {
        this(id, artesanoId, titulo, descripcion, montoPrecio, moneda, stock, estado, grupo, imagenes, fechaPublicacion, precioConDescuento, porcentajeDescuento, null, null);
    }
}
