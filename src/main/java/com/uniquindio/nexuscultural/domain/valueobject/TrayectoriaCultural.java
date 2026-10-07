package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import java.util.Collections;
import java.util.List;

public record TrayectoriaCultural(
        int anosExperiencia,
        List<String> reconocimientos
) {
    public TrayectoriaCultural {
        if (anosExperiencia < 0) {
            throw new ReglaDominioException("Los años de experiencia no pueden ser negativos");
        }
        // Evita NullPointerException y asegura inmutabilidad de la lista
        reconocimientos = (reconocimientos == null) ? Collections.emptyList() : List.copyOf(reconocimientos);
    }

    // Método de apoyo para agregar un nuevo reconocimiento o hito
    public TrayectoriaCultural agregarReconocimiento(String nuevoReconocimiento) {
        if (nuevoReconocimiento == null || nuevoReconocimiento.isBlank()) {
            throw new ReglaDominioException("El reconocimiento no puede estar vacío");
        }
        var nuevaLista = new java.util.ArrayList<>(this.reconocimientos);
        nuevaLista.add(nuevoReconocimiento);
        return new TrayectoriaCultural(this.anosExperiencia, nuevaLista);
    }
}
