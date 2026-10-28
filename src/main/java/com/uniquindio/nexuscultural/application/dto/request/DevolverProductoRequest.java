package com.uniquindio.nexuscultural.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

// Mapea a: Devolucion.crear()
// DTO inmutable que transporta los datos requeridos desde la capa externa
public record DevolverProductoRequest(
        @JsonAlias({"idPedido", "pedidoId"})
        UUID idPedido,

        @JsonAlias({"idComprador", "compradorId"})
        UUID idComprador,

        @NotBlank(message = "El motivo es obligatorio")
        @Size(min = 3, max = 100)
        String motivo,

        @NotBlank(message = "Se debe agregar una descripción del motivo")
        @Size(min = 3, max = 500)
        @JsonAlias({"descripcionEstado", "comentario", "descripcion"})
        String descripcionEstado
) {}

