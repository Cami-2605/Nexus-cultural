package com.uniquindio.nexuscultural.application.dto.request;

import java.util.UUID;

// DTO inmutable que transporta los datos requeridos desde la capa externa
public record DevolverProductoRequest(
        UUID idPedido,
        UUID idComprador,
        String motivo,
        String descripcionEstado
) {}
