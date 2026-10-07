package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Administrador extends Usuario {

    private String nivelAcceso;

    @Builder
    private Administrador(UUID id,
                         String nombreCompleto,
                         Email email,
                         LocalDateTime fechaRegistro,
                         String nivelAcceso) {
        super(id, nombreCompleto, email, RolUsuario.ADMIN, fechaRegistro);
        this.nivelAcceso = nivelAcceso != null ? nivelAcceso : "SUPER_ADMIN";
    }

    public void cambiarNivelAcceso(String nuevoNivel) {
        this.nivelAcceso = nuevoNivel;
    }
}