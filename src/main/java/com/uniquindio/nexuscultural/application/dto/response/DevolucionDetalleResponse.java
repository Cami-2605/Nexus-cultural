package com.uniquindio.nexuscultural.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record DevolucionDetalleResponse(
        UUID pedidoId,
        UUID compradorId,
        String compradorNombre,
        UUID articuloId,
        String articuloTitulo,
        int cantidad,
        BigDecimal montoTotal,
        String moneda,
        String motivoDevolucion,
        String estadoDevolucion,
        LocalDateTime fechaSolicitudDevolucion,
        String metodoPago,
        String referenciaTransaccion
) {
}
