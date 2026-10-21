package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.Logro;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Artesano extends Usuario {

    private TrayectoriaCultural trayectoriaCultural;
    private UbicacionGeografica ubicacionGeografica;
    private boolean activo;
    private final List<Logro> logros = new ArrayList<>();

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

    /**
     * Otorga un logro al artesano. Si ya lo tiene, no lo agrega de nuevo (idempotente).
     */
    public void otorgarLogro(Logro logro) {
        if (logro == null) throw new ReglaDominioException("El logro no puede ser nulo.");
        if (!logros.contains(logro)) {
            logros.add(logro);
        }
    }

    /** Retorna la lista de logros del artesano (inmutable). */
    public List<Logro> getLogros() {
        return Collections.unmodifiableList(logros);
    }
}