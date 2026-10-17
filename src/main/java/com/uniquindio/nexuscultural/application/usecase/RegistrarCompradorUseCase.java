package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistrarCompradorUseCase {

    private final CompradorRepository compradorRepository;
    private final ArtesanoRepository artesanoRepository;
    private final AdministradorRepository administradorRepository;

    public RegistrarCompradorUseCase(CompradorRepository compradorRepository,
                                     ArtesanoRepository artesanoRepository,
                                     AdministradorRepository administradorRepository) {
        this.compradorRepository = compradorRepository;
        this.artesanoRepository = artesanoRepository;
        this.administradorRepository = administradorRepository;
    }

    public Comprador ejecutar(String nombreCompleto, String emailTexto, UbicacionGeografica direccion) {
        Email email = new Email(emailTexto);

        // 1. Validar si ya existe como Comprador
        compradorRepository.buscarPorEmail(email).ifPresent(c -> {
            throw new ReglaDominioException("Ya existe un comprador registrado con el correo: " + emailTexto);
        });

        // 2. Validar si ya existe como Artesano
        artesanoRepository.buscarPorEmail(email).ifPresent(art -> {
            throw new ReglaDominioException("Ya existe un artesano registrado con el correo: " + emailTexto);
        });

        // 3. Validar si ya existe como Administrador
        administradorRepository.buscarPorEmail(email).ifPresent(admin -> {
            throw new ReglaDominioException("Ya existe un administrador registrado con el correo: " + emailTexto);
        });

        Comprador nuevoComprador = Comprador.registrar(nombreCompleto, email, direccion, LocalDateTime.now());
        return compradorRepository.guardar(nuevoComprador);
    }
}