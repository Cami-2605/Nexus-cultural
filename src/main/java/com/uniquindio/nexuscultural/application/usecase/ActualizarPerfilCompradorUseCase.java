package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActualizarPerfilCompradorUseCase {

    private final CompradorRepository compradorRepository;

    public ActualizarPerfilCompradorUseCase(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
    }

    public Comprador ejecutar(UUID id, String nuevoNombreCompleto) {
        Comprador comprador = compradorRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado con el ID: " + id));

        comprador.actualizarPerfil(nuevoNombreCompleto);
        return compradorRepository.guardar(comprador);
    }
}