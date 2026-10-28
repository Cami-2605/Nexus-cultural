package com.uniquindio.nexuscultural.application.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CrearPedidoRequest(
        @NotNull(message = "El comprador es obligatorio")
        @JsonAlias({"compradorId", "idComprador"})
        UUID compradorId,

        @NotNull(message = "El artículo es obligatorio")
        @JsonAlias({"articuloId", "idArticulo"})
        UUID articuloId,

        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        int cantidad
) {}
