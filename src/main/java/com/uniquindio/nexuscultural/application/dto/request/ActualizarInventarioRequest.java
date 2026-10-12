package com.uniquindio.nexuscultural.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

// DTO inmutable que transporta los datos requeridos desde la capa externa
public record ActualizarInventarioRequest(
        UUID idArticulo,
        UUID idArtesano,
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad,
        @NotBlank(message = "El motivo de la actualización es obligatorio")
        @Size(min = 3, max = 100)
        String motivo
) {}
