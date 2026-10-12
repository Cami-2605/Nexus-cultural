package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GestionarFavoritosUseCase {

    private final CompradorRepository compradorRepository;

    public GestionarFavoritosUseCase(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
    }

    public Comprador agregarFavorito(UUID compradorId, UUID articuloId) {
        Comprador comprador = obtenerComprador(compradorId);
        comprador.agregarFavorito(articuloId);
        return compradorRepository.guardar(comprador);
    }

    public List<UUID> consultarFavoritos(UUID compradorId) {
        Comprador comprador = obtenerComprador(compradorId);
        return comprador.getFavoritos();
    }

    public Comprador eliminarFavorito(UUID compradorId, UUID articuloId) {
        Comprador comprador = obtenerComprador(compradorId);
        comprador.quitarFavorito(articuloId);
        return compradorRepository.guardar(comprador);
    }

    private Comprador obtenerComprador(UUID id) {
        return compradorRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado con el ID: " + id));
    }
}