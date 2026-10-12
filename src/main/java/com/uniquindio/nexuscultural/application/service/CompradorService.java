package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarDireccionRequest;
import com.uniquindio.nexuscultural.application.dto.request.ActualizarPerfilCompradorRequest;
import com.uniquindio.nexuscultural.application.dto.request.RegistrarCompradorRequest;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CompradorService {

    private final CompradorRepository compradorRepository;

    public CompradorService(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
    }

    public Comprador registrarComprador(RegistrarCompradorRequest request) {
        Email email = new Email(request.email());

        // Verificación de email único antes de registrar
        compradorRepository.buscarPorEmail(email).ifPresent(c -> {
            throw new ReglaDominioException("Ya existe un comprador registrado con el correo: " + request.email());
        });

        UbicacionGeografica direccion = new UbicacionGeografica(
                request.departamento(),
                request.municipio(),
                request.direccion()
        );

        Comprador comprador = Comprador.registrar(
                request.nombreCompleto(),
                email,
                direccion,
                LocalDateTime.now()
        );

        return compradorRepository.guardar(comprador);
    }

    public Comprador obtenerPorId(UUID compradorId) {
        return compradorRepository.buscarPorId(compradorId)
                .orElseThrow(() -> new ReglaDominioException("Comprador no encontrado."));
    }

    public Comprador actualizarPerfil(UUID compradorId, ActualizarPerfilCompradorRequest request) {
        Comprador comprador = obtenerPorId(compradorId);
        comprador.actualizarPerfil(request.nombreCompleto());
        return compradorRepository.guardar(comprador);
    }

    public Comprador actualizarDireccionEntrega(UUID compradorId, ActualizarDireccionRequest request) {
        Comprador comprador = obtenerPorId(compradorId);

        UbicacionGeografica nuevaDireccion = new UbicacionGeografica(
                request.departamento(),
                request.municipio(),
                request.direccion()
        );

        comprador.actualizarDireccionEntrega(nuevaDireccion);
        return compradorRepository.guardar(comprador);
    }

    public Comprador agregarFavorito(UUID compradorId, UUID articuloId) {
        Comprador comprador = obtenerPorId(compradorId);
        // La entidad Comprador valida el máximo de 15 favoritos y duplicados
        comprador.agregarFavorito(articuloId);
        return compradorRepository.guardar(comprador);
    }

    public List<UUID> consultarFavoritos(UUID compradorId) {
        Comprador comprador = obtenerPorId(compradorId);
        return comprador.getFavoritos();
    }

    public Comprador eliminarFavorito(UUID compradorId, UUID articuloId) {
        Comprador comprador = obtenerPorId(compradorId);
        // La entidad Comprador lanza ReglaDominioException si no existe en la lista
        comprador.quitarFavorito(articuloId);
        return compradorRepository.guardar(comprador);
    }
}