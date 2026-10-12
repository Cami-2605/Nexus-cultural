package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Artesano extends Usuario {

    private TrayectoriaCultural trayectoriaCultural;
    private UbicacionGeografica ubicacionGeografica;
    private boolean activo;

    @Builder
    private Artesano(UUID id,
                     String nombreCompleto,
                     Email email,
                     LocalDateTime fechaRegistro,
                     TrayectoriaCultural trayectoriaCultural,
                     UbicacionGeografica ubicacionGeografica,
                     Boolean activo) {
        super(id, nombreCompleto, email, RolUsuario.ARTESANO, fechaRegistro);
        this.trayectoriaCultural = trayectoriaCultural;
        this.ubicacionGeografica = ubicacionGeografica;
        this.activo = activo != null ? activo : true;
    }

    // Método de fábrica para registros nuevos (Encapsula todas las validaciones de creación)
    public static Artesano registrar(String nombreCompleto,
                                     Email email,
                                     TrayectoriaCultural trayectoria,
                                     UbicacionGeografica ubicacion,
                                     LocalDateTime ahora) {

        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new ReglaDominioException("El nombre completo del artesano es obligatorio.");
        }
        if (trayectoria == null) {
            throw new ReglaDominioException("La trayectoria cultural del artesano es obligatoria.");
        }
        if (ubicacion == null) {
            throw new ReglaDominioException("La ubicación geográfica del artesano es obligatoria.");
        }

        return Artesano.builder()
                .id(UUID.randomUUID())
                .nombreCompleto(nombreCompleto.trim())
                .email(email)
                .fechaRegistro(ahora)
                .trayectoriaCultural(trayectoria)
                .ubicacionGeografica(ubicacion)
                .activo(true)
                .build();
    }

    public void actualizarPerfil(String nuevoNombreCompleto) {
        if (nuevoNombreCompleto == null || nuevoNombreCompleto.trim().isEmpty()) {
            throw new ReglaDominioException("El nombre completo no puede estar vacío.");
        }
        this.nombreCompleto = nuevoNombreCompleto.trim();
    }

    public void actualizarTrayectoria(TrayectoriaCultural nuevaTrayectoria) {
        if (nuevaTrayectoria == null) {
            throw new ReglaDominioException("La trayectoria cultural del artesano no puede ser nula.");
        }
        this.trayectoriaCultural = nuevaTrayectoria;
    }

    public void desactivarPerfil() {
        this.activo = false;
    }

    public void activarPerfil() {
        this.activo = true;
    }
}