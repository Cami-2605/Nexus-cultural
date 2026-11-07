package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ConfirmarPagoWompiRequest(
        @NotNull(message = "El id del pedido es obligatorio.")
        UUID idPedido,
        String transaccionId,
        String referencia,
        String metodoPago,
        String estado,
        String numeroTarjeta,
        String cvc,
        String expMonth,
        String expYear,
        String titularTarjeta,
        Integer cuotas,
        String emailComprador
) {
}
