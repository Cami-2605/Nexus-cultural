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