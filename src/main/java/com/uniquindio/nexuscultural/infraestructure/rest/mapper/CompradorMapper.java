package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.application.dto.response.CompradorResponse;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import org.springframework.stereotype.Component;

@Component
public class CompradorMapper {

    public CompradorResponse toResponse(Comprador comprador) {
        if (comprador == null) {
            return null;
        }

        return new CompradorResponse(
                comprador.getId(),
                comprador.getNombreCompleto(),
                comprador.getEmail().valor(),
                comprador.getFechaRegistro(),
                comprador.getDireccionEntrega(),
                comprador.getFavoritos()
        );
    }
}