package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

// Mapea a: Devoucion.crear()
// DTO inmutable que transporta los datos requeridos desde la capa externa
public record DevolverProductoRequest(
        UUID idPedido,
        UUID idComprador,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 3, max = 100)
        String motivo,

        @NotBlank(message = "Se debe agregrar una descripción del motivo")
        @Size(min = 3, max = 100)
        String descripcionEstado
) {}
