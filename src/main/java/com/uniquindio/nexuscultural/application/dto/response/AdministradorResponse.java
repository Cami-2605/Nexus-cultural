package com.uniquindio.nexuscultural.application.dto.response;

import com.uniquindio.nexuscultural.domain.entity.Administrador;

import java.time.LocalDateTime;
import java.util.UUID;
public record AdministradorResponse(
        UUID id,
        String nombreCompleto,
        String email,
        LocalDateTime fechaRegistro,
        String nivelAcceso
) {
    public static AdministradorResponse desdeDominio(Administrador admin) {
        return new AdministradorResponse(
                admin.getId(),
                admin.getNombreCompleto(),
                admin.getEmail().valor(),
                admin.getFechaRegistro(),
                admin.getNivelAcceso().name() //.name() convierte el Enum NivelAcceso a String
        );
    }
}