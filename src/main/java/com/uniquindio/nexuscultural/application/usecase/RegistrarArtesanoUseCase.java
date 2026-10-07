package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;

import java.time.LocalDateTime;
import java.util.UUID;

public class RegistrarArtesanoUseCase {

    private final ArtesanoRepository artesanoRepository;

    public RegistrarArtesanoUseCase(ArtesanoRepository artesanoRepository) {
        this.artesanoRepository = artesanoRepository;
    }

    public Artesano ejecutar(String nombreCompleto,
                             String emailTexto,
                             TrayectoriaCultural trayectoria,
                             UbicacionGeografica ubicacion) {

        // Crear e instanciar el Value Object Email (su constructor valida la sintaxis)
        Email email = new Email(emailTexto);

        // Validar que el nombre completo no venga nulo o vacío
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new ReglaDominioException("El nombre completo del artesano es obligatorio.");
        }

        // Regla de dominio: Verificar que no exista otro artesano registrado con el mismo correo
        if (artesanoRepository.buscarPorEmail(email).isPresent()) {
            throw new ReglaDominioException("Ya existe un artesano registrado con el correo: " + emailTexto);
        }

        // Regla de dominio: Verificar que la trayectoria cultural no sea nula
        if (trayectoria == null) {
            throw new ReglaDominioException("La trayectoria cultural del artesano es obligatoria.");
        }

        // Construir la entidad Artesano con sus atributos iniciales
        Artesano nuevoArtesano = Artesano.builder()
                .id(UUID.randomUUID())
                .nombreCompleto(nombreCompleto.trim())
                .email(email)
                .fechaRegistro(LocalDateTime.now())
                .trayectoriaCultural(trayectoria)
                .ubicacionGeografica(ubicacion)
                .activo(true)
                .build();

        // Persistir mediante el repositorio y retornar
        return artesanoRepository.guardar(nuevoArtesano);
    }
}