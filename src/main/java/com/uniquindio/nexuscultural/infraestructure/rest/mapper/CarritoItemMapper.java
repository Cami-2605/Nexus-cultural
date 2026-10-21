package com.uniquindio.nexuscultural.infraestructure.rest.mapper;

import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.CarritoItemEntity;
import org.springframework.stereotype.Component;

@Component
public class CarritoItemMapper {

    public CarritoItemEntity toEntity(CarritoItem domain) {
        if (domain == null) return null;

        return CarritoItemEntity.builder()
                .id(domain.getId())
                .compradorId(domain.getCompradorId())
                .articuloId(domain.getArticuloId())
                .cantidad(domain.getCantidad())
                .build();
    }

    public CarritoItem toDomain(CarritoItemEntity entity) {
        if (entity == null) return null;

        // Usa el método estático de fábrica del dominio para reconstruir la instancia sin violar reglas
        return CarritoItem.reconstruir(
                entity.getId(),
                entity.getCompradorId(),
                entity.getArticuloId(),
                entity.getCantidad()
        );
    }
}