package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrarArtesanoUseCase {

    private final ArtesanoRepository artesanoRepository;
    private final CompradorRepository compradorRepository;
    private final AdministradorRepository administradorRepository;

    public RegistrarArtesanoUseCase(ArtesanoRepository artesanoRepository,
                                    CompradorRepository compradorRepository,
                                    AdministradorRepository administradorRepository) {
        this.artesanoRepository = artesanoRepository;
        this.compradorRepository = compradorRepository;
        this.administradorRepository = administradorRepository;
    }

    @Transactional
    public Artesano ejecutar(String nombreCompleto,
                             String emailTexto,
                             TrayectoriaCultural trayectoria,
                             UbicacionGeografica ubicacion) {

        Email email = new Email(emailTexto);

         // 1. Validar si ya existe como Artesano
        artesanoRepository.buscarPorEmail(email).ifPresent(a -> {
            throw new ReglaDominioException("Ya existe un artesano registrado con el correo: " + emailTexto);
        });

        // 2. Validar si ya existe como Comprador
        compradorRepository.buscarPorEmail(email).ifPresent(c -> {
            throw new ReglaDominioException("Ya existe un comprador registrado con el correo: " + emailTexto);
        });

        // 3. Validar si ya existe como Administrador
        administradorRepository.buscarPorEmail(email).ifPresent(admin -> {
            throw new ReglaDominioException("Ya existe un administrador registrado con el correo: " + emailTexto);
        });

        // La entidad valida sus propios atributos obligatorios y se instancia
        Artesano nuevoArtesano = Artesano.registrar(nombreCompleto, email, trayectoria, ubicacion, LocalDateTime.now());

        // El caso de uso solo persiste y retorna
        return artesanoRepository.guardar(nuevoArtesano);
    }
}