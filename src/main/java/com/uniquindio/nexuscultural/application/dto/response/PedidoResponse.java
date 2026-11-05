package com.uniquindio.nexuscultural.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PedidoResponse(
        UUID id,
        UUID compradorId,
        UUID articuloId,
        int cantidad,
        BigDecimal totalMonto,
        String totalMoneda,
        String estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaEntrega,
        String metodoPago,
        String referenciaTransaccion
) {}
