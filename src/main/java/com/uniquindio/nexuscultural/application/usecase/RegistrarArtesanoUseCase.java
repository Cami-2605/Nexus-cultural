package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
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

    public RegistrarArtesanoUseCase(ArtesanoRepository artesanoRepository) {
        this.artesanoRepository = artesanoRepository;
    }

    @Transactional
    public Artesano ejecutar(String nombreCompleto,
                             String emailTexto,
                             TrayectoriaCultural trayectoria,
                             UbicacionGeografica ubicacion) {

        Email email = new Email(emailTexto);

        // Verificación de existencia sin bloque 'if', delegada a la API funcional de Optional
        artesanoRepository.buscarPorEmail(email).ifPresent(a -> {
            throw new ReglaDominioException("Ya existe un artesano registrado con el correo: " + emailTexto);
        });

        // La entidad valida sus propios atributos obligatorios y se instancia
        Artesano nuevoArtesano = Artesano.registrar(nombreCompleto, email, trayectoria, ubicacion, LocalDateTime.now());

        // El caso de uso solo persiste y retorna
        return artesanoRepository.guardar(nuevoArtesano);
    }
}