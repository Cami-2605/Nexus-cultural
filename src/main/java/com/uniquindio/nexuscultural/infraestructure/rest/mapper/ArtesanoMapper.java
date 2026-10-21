package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.application.dto.response.ArtesanoResponse;
import com.uniquindio.nexuscultural.application.dto.response.LogroResponse;
import com.uniquindio.nexuscultural.domain.entity.Artesano;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper que convierte la entidad {@link Artesano} al DTO {@link ArtesanoResponse}.
 */
@Component
public class ArtesanoMapper {

    public ArtesanoResponse toResponse(Artesano artesano) {
        if (artesano == null) {
            return null;
        }

        List<LogroResponse> logros = artesano.getLogros().stream()
                .map(l -> new LogroResponse(l.getNombre(), l.getDescripcion()))
                .toList();

        return new ArtesanoResponse(
                artesano.getId(),
                artesano.getNombreCompleto(),
                artesano.getEmail().valor(),
                artesano.getFechaRegistro(),
                artesano.isActivo(),
                artesano.getTrayectoriaCultural(),
                artesano.getUbicacionGeografica(),
                logros
        );
    }
}
