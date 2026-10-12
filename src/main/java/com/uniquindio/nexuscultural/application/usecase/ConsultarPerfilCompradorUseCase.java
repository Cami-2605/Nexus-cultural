package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ConsultarPerfilCompradorUseCase {

    private final CompradorRepository compradorRepository;

    public ConsultarPerfilCompradorUseCase(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
    }

    public Comprador ejecutar(UUID id) {
        return compradorRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado con el ID: " + id));
    }
}