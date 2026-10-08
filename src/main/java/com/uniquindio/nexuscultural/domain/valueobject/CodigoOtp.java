package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;

// El código OTP es un valor inmutable que representa una prueba de identidad temporal
public record CodigoOtp(
        String codigo,
        LocalDateTime fechaExpiracion,
        EstadoOtp estado
) {
    public CodigoOtp {
        if (codigo == null || !codigo.matches("\\d{6}")) {
            throw new ReglaDominioException("El código OTP debe ser un número de 6 dígitos.");
        }
        Objects.requireNonNull(fechaExpiracion, "La fecha de expiración no puede ser nula.");
        Objects.requireNonNull(estado, "El estado no puede ser nulo.");
    }

    public static CodigoOtp generar(int minutosValidez) {
        String codigoGenerado = String.format("%06d", new Random().nextInt(1000000));
        return new CodigoOtp(codigoGenerado, LocalDateTime.now().plusMinutes(minutosValidez), EstadoOtp.PENDIENTE);
    }

    public boolean esValido(String codigoIngresado) {
        return this.estado == EstadoOtp.PENDIENTE && !estaExpirado() && this.codigo.equals(codigoIngresado);
    }

    public boolean estaExpirado() {
        return LocalDateTime.now().isAfter(fechaExpiracion);
    }

    public CodigoOtp marcarComoUsado() {
        return new CodigoOtp(this.codigo, this.fechaExpiracion, EstadoOtp.USADO);
    }
}