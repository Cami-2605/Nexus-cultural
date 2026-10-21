package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.NivelAcceso;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Administrador extends Usuario {

    private NivelAcceso nivelAcceso;

    @Builder
    private Administrador(UUID id, String nombreCompleto, Email email, LocalDateTime fechaRegistro, NivelAcceso nivelAcceso) {
        super(id, nombreCompleto, email, RolUsuario.ADMIN, fechaRegistro);
        this.nivelAcceso = nivelAcceso != null ? nivelAcceso : NivelAcceso.SUPER_ADMIN;
    }

    public void cambiarNivelAcceso(NivelAcceso nuevoNivel) {
        if (nuevoNivel == null) {
            throw new ReglaDominioException("El nivel de acceso no puede ser nulo.");
        }
        this.nivelAcceso = nuevoNivel;
    }

    public void actualizarDatos(String nuevoNombre, NivelAcceso nuevoNivel) {
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            this.nombreCompleto = nuevoNombre.trim();
        }
        if (nuevoNivel != null) {
            this.nivelAcceso = nuevoNivel;
        }
    }
}