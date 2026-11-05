package com.uniquindio.nexuscultural.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// Mapea a: Pedido.procesarPago()
// DTO inmutable que transporta los datos requeridos desde la capa externa
public record ProcesarPagoRequest(
        @NotNull(message = "El ID del pedido es obligatorio")
        @JsonAlias({"idPedido", "pedidoId"})
        UUID idPedido,

        @JsonAlias({"idComprador", "compradorId"})
        UUID idComprador,

        @NotNull(message = "El monto del pago es obligatorio")
        Precio monto,

        @NotBlank(message = "Se debe seleccionar un método de pago")
        String metodoPago,

        String referenciaTransaccion
) {}

