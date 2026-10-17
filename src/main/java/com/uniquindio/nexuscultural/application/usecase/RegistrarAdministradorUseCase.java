package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.NivelAcceso;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrarAdministradorUseCase {

    private final AdministradorRepository administradorRepository;
    private final CompradorRepository compradorRepository;
    private final ArtesanoRepository artesanoRepository;

    public RegistrarAdministradorUseCase(AdministradorRepository administradorRepository,
                                         CompradorRepository compradorRepository,
                                         ArtesanoRepository artesanoRepository) {
        this.administradorRepository = administradorRepository;
        this.compradorRepository = compradorRepository;
        this.artesanoRepository = artesanoRepository;
    }

    @Transactional
    public Administrador ejecutar(String nombreCompleto, String emailTexto, NivelAcceso nivelAcceso) {
        Email email = new Email(emailTexto);
        // 2. Validar si ya existe como Admin
        administradorRepository.buscarPorEmail(email).ifPresent(a -> {
            throw new ReglaDominioException("Ya existe un administrador registrado con el correo: " + emailTexto);
        });
        // 2. Validar si ya existe como Comprador
        compradorRepository.buscarPorEmail(email).ifPresent(c -> {
            throw new ReglaDominioException("Ya existe un comprador registrado con el correo: " + emailTexto);
        });

        // 3. Validar si ya existe como Artesano
        artesanoRepository.buscarPorEmail(email).ifPresent(art -> {
            throw new ReglaDominioException("Ya existe un artesano registrado con el correo: " + emailTexto);
        });

        Administrador nuevoAdmin = Administrador.builder()
                .id(java.util.UUID.randomUUID())
                .nombreCompleto(nombreCompleto)
                .email(email)
                .fechaRegistro(LocalDateTime.now())
                .nivelAcceso(nivelAcceso)
                .build();

        return administradorRepository.guardar(nuevoAdmin);
    }
}