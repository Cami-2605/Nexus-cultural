package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.RolUsuario;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Comprador extends Usuario {

    private static final int MAX_FAVORITOS = 15;

    private final List<UUID> favoritos;

    private UbicacionGeografica direccionEntrega;

    @Builder
    private Comprador(UUID id,
                     String nombreCompleto,
                     Email email,
                     LocalDateTime fechaRegistro, List<UUID> favoritos,
                     UbicacionGeografica direccionEntrega) {
        super(id, nombreCompleto, email, RolUsuario.COMPRADOR, fechaRegistro);
        this.favoritos = new ArrayList<>(favoritos);
        this.direccionEntrega = direccionEntrega;
    }

    public static Comprador registrar(String nombreCompleto, Email email,
                                      UbicacionGeografica direccionEntrega, LocalDateTime ahora) {
        if (direccionEntrega == null) {
            throw new ReglaDominioException("El comprador debe tener una dirección de entrega.");
        }
        return new Comprador(UUID.randomUUID(), nombreCompleto, email, ahora, List.of(), direccionEntrega)
                ;
    }

    public void agregarFavorito(UUID articuloId) {
        if (articuloId == null) {
            throw new ReglaDominioException("El artículo favorito no puede ser nulo.");
        }
        if (favoritos.contains(articuloId)) {
            throw new ReglaDominioException("Este artículo ya está en la lista de favoritos.");
        }
        if (favoritos.size() >= MAX_FAVORITOS) {
            throw new ReglaDominioException(
                    "El comprador no puede tener más de " + MAX_FAVORITOS + " artículos favoritos.");
        }
        favoritos.add(articuloId);
    }

    public void quitarFavorito(UUID articuloId) {
        if (!favoritos.remove(articuloId)) {
            throw new ReglaDominioException("Este artículo no está en la lista de favoritos.");
        }
    }

    public void actualizarDireccionEntrega(UbicacionGeografica nuevaDireccion) {
        this.direccionEntrega = nuevaDireccion;
    }
}