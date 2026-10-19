package com.uniquindio.nexuscultural.infraestructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "articulos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 1000)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precioMonto;

    @Column(nullable = false, length = 3)
    private String precioMoneda;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private String grupo;

    @Column(name = "artesano_id", nullable = false)
    private UUID artesanoId;

    @ElementCollection
    @CollectionTable(name = "articulo_imagenes", joinColumns = @JoinColumn(name = "articulo_id"))
    @Column(name = "imagen_url")
    @Builder.Default
    private List<String> imagenes = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime fechaPublicacion;
}