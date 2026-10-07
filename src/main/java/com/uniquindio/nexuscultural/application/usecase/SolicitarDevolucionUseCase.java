package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Devolucion;

import java.util.UUID;

public interface SolicitarDevolucionUseCase {

    Devolucion ejecutar(Comando comando);

    record Comando(UUID pedidoId, UUID compradorId, String motivo) { }

}
