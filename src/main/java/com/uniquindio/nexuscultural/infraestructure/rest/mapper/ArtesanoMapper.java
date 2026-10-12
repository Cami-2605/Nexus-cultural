package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.application.dto.response.ArtesanoResponse;
import com.uniquindio.nexuscultural.domain.entity.Artesano;
import org.springframework.stereotype.Component;

@Component
public class ArtesanoMapper {

    public ArtesanoResponse toResponse(Artesano artesano) {
        if (artesano == null) {
            return null;
        }

        return new ArtesanoResponse(
                artesano.getId(),
                artesano.getNombreCompleto(),
                artesano.getEmail().valor(),
                artesano.getFechaRegistro(),
                artesano.isActivo(),
                artesano.getTrayectoriaCultural(),
                artesano.getUbicacionGeografica()
        );
    }
}