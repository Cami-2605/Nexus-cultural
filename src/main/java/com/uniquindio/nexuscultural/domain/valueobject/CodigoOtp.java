package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

// El código OTP es un valor inmutable que representa una prueba de identidad temporal
public record CodigoOtp(
        String codigo,
        LocalDateTime fechaExpiracion,
        EstadoOtp estado,
        int intentosFallidos
) {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public CodigoOtp(String codigo, LocalDateTime fechaExpiracion, EstadoOtp estado) {
        this(codigo, fechaExpiracion, estado, 0);
    }

    public CodigoOtp {
        if (codigo == null || codigo.isBlank()) {
            throw new ReglaDominioException("El código OTP no puede estar vacío.");
        }
        Objects.requireNonNull(fechaExpiracion, "La fecha de expiración no puede ser nula.");
        Objects.requireNonNull(estado, "El estado no puede ser nulo.");
    }

    public static CodigoOtp generar(int minutosValidez) {
        int numero = SECURE_RANDOM.nextInt(1000000);
        String codigoGenerado = String.format("%06d", numero);
        return new CodigoOtp(codigoGenerado, LocalDateTime.now().plusMinutes(minutosValidez), EstadoOtp.PENDIENTE, 0);
    }

    public boolean esValido(String codigoIngresado) {
        return this.estado == EstadoOtp.PENDIENTE && !estaExpirado() && this.codigo.equals(codigoIngresado);
    }

    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }

    public CodigoOtp marcarComoUsado() {
        return new CodigoOtp(this.codigo, this.fechaExpiracion, EstadoOtp.USADO, this.intentosFallidos);
    }

    public CodigoOtp incrementarIntentos() {
        int nuevosIntentos = this.intentosFallidos + 1;
        EstadoOtp nuevoEstado = nuevosIntentos >= 3 ? EstadoOtp.EXPIRADO : this.estado;
        return new CodigoOtp(this.codigo, this.fechaExpiracion, nuevoEstado, nuevosIntentos);
    }
}