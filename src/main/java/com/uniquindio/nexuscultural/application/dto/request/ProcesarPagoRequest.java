package com.uniquindio.nexuscultural.application.dto.request;

import com.uniquindio.nexuscultural.domain.valueobject.Precio;

import java.math.BigDecimal;
import java.util.UUID;

// DTO inmutable que transporta los datos requeridos desde la capa externa
public record ProcesarPagoRequest(
        UUID idPedido,
        UUID idComprador,
        Precio monto,
        String metodoPago,
        String referenciaTransaccion
) {}
