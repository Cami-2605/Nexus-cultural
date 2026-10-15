package com.uniquindio.nexuscultural.infraestructure.persistence.entity;

import com.uniquindio.nexuscultural.domain.valueobject.NivelAcceso;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import jakarta.persistence.*;
        import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "administradores")
@Getter
@Setter
public class AdministradorEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NivelAcceso nivelAcceso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;


}