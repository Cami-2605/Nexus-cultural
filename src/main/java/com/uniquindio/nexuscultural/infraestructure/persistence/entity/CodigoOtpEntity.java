package com.uniquindio.nexuscultural.infraestructure.persistence.entity;

import com.uniquindio.nexuscultural.domain.valueobject.EstadoOtp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "codigos_otp")
@Getter
@Setter
public class CodigoOtpEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private LocalDateTime fechaExpiracion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoOtp estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
}