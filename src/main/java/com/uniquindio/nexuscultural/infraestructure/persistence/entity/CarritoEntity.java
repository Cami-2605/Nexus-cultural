package com.uniquindio.nexuscultural.infraestructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "carritos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoEntity {

    @Id
    private UUID id;

    @Column(name = "comprador_id", nullable = false, unique = true)
    private UUID compradorId;

    @Column(precision = 12, scale = 2)
    private BigDecimal total;
}