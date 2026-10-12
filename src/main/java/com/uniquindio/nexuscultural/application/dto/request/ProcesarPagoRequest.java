package com.uniquindio.nexuscultural.application.dto.request;

import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;
// Mapea a: Pedido.procesarPago()
// DTO inmutable que transporta los datos requeridos desde la capa externa
public record ProcesarPagoRequest(
        UUID idPedido,
        UUID idComprador,
        @Min(value = 1, message = "El precio debe ser al menos $ 1")
        Precio monto,

        @NotBlank(message = "Se debe seleccionar un método de pago")
        String metodoPago,
        //@NotBlank(message = "El departamento es obligatorio")
        String referenciaTransaccion
) {}
