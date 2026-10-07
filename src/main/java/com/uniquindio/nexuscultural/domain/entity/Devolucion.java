package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoDevolucion;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
public class Devolucion {

    private final UUID id;
    private final UUID pedidoId;
    private String motivo;
    private final LocalDateTime fechaSolicitud;

    private  EstadoDevolucion estado;

    private Devolucion(UUID id, UUID pedidoId, String motivo, LocalDateTime fechaSolicitud) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.motivo = motivo;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = EstadoDevolucion.EN_PROCESO;
    }

    public static Devolucion crear(UUID pedidoId, String motivo, LocalDateTime ahora) {
        if (pedidoId == null) {
            throw new ReglaDominioException("La devolución debe estar asociada a un pedido.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new ReglaDominioException("La solicitud de devolución debe incluir una razón.");
        }
        return new Devolucion(UUID.randomUUID(), pedidoId, motivo, ahora);
    }

    public void verificarProceso(){
        if (estado != EstadoDevolucion.EN_PROCESO){
            throw new ReglaDominioException("La solicitud ya fue resuelta.");
        }
    }

    public void aprobar(){

        verificarProceso();
        estado = EstadoDevolucion.APROBADO;

    }

    public void rechazar(){
        verificarProceso();
        estado = EstadoDevolucion.RECHAZADO;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Devolucion otro)) return false;
        return id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }




}
