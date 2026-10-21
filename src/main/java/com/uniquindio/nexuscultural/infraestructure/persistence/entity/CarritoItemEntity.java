package com.uniquindio.nexuscultural.infraestructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "carrito_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoItemEntity {

    @Id
    private UUID id;

    @Column(name = "comprador_id", nullable = false)
    private UUID compradorId;

    @Column(name = "articulo_id", nullable = false)
    private UUID articuloId;

    @Column(nullable = false)
    private Integer cantidad;
}