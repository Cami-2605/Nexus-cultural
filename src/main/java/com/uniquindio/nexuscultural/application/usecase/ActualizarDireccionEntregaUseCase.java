package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActualizarDireccionEntregaUseCase {

    private final CompradorRepository compradorRepository;

    public ActualizarDireccionEntregaUseCase(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
    }

    public Comprador ejecutar(UUID id, UbicacionGeografica nuevaDireccion) {
        Comprador comprador = compradorRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado con el ID: " + id));

        comprador.actualizarDireccionEntrega(nuevaDireccion);
        return compradorRepository.guardar(comprador);
    }
}