package com.uniquindio.nexuscultural.domain.valueobject;


import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;

public record Telefono(String numero) {

    // Regex para validar formato internacional o celular de Colombia (10 dígitos)
    private static final String REGEX_TELEFONO = "^(\\+?57)?3\\d{9}$";

    // Constructor compacto: se ejecuta automáticamente al instanciar
    public Telefono {
        if (numero == null || numero.isBlank()) {
            throw new ReglaDominioException("El número de teléfono es obligatorio.");
        }

        // Limpia espacios en blanco o guiones
        numero = numero.replaceAll("[\\s-]", "");

        if (!numero.matches(REGEX_TELEFONO)) {
            throw new ReglaDominioException("El número de teléfono no cumple con un formato celular válido.");
        }
    }

    // Método de utilidad para formatear la salida (ej: para enviar SMS)
    public String obtenerFormatoInternacional() {
        if (!numero.startsWith("+57")) {
            return "+57" + numero;
        }
        return numero;
    }
}
