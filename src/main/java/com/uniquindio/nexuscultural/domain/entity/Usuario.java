package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class Usuario {

    @EqualsAndHashCode.Include
    protected final UUID id;

    protected String nombreCompleto;
    protected Email email;
    protected RolUsuario rol;
    protected final LocalDateTime fechaRegistro;

    protected Usuario(UUID id, String nombreCompleto, Email email, RolUsuario rol, LocalDateTime fechaRegistro) {
        this.id = id != null ? id : UUID.randomUUID();
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro != null ? fechaRegistro : LocalDateTime.now();
    }

    public boolean esAdministrador() {
        return RolUsuario.ADMIN.equals(this.rol);
    }

    public boolean esArtesano() {
        return RolUsuario.ARTESANO.equals(this.rol);
    }

    public boolean esComprador() {
        return RolUsuario.COMPRADOR.equals(this.rol);
    }
}