package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.regex.Pattern;

public record Email(String valor) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public Email {
        if (valor == null || valor.isBlank()) {
            throw new ReglaDominioException("El correo electrónico no puede estar vacío.");
        }
        valor = valor.trim();
        if (!EMAIL_PATTERN.matcher(valor).matches()) {
            throw new ReglaDominioException("El formato del correo electrónico no es válido: " + valor);
        }
    }

    @Override
    public String toString() {
        return valor;
    }
}