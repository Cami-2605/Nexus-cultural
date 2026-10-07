package com.uniquindio.nexuscultural.domain.entity;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoArticulo;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ArticuloTest {

    private final UUID artesanoId = UUID.randomUUID();
    private final Precio precio = new Precio(new BigDecimal("30000"), "COP");
    private final List<String> dosImagenes = List.of("foto1.jpg", "foto2.jpg");

    @Test
    void dosArticulosConLaMismaIdentidadSonElMismoAunqueCambienSusDatos() {
        // Arrange: mismo id, diferentes datos
        UUID mismoId = UUID.randomUUID();
        Articulo original = Articulo.reconstruir(mismoId, artesanoId, "Poncho de lana",
                "Poncho tejido a mano", 3, EstadoArticulo.DISPONIBLE, Grupo.TEJIDOS,
                dosImagenes, precio, LocalDateTime.now());
        Articulo otraVersion = Articulo.reconstruir(mismoId, artesanoId, "Ruana bordada",
                "Otra descripción", 10, EstadoArticulo.AGOTADO, Grupo.TEJIDOS,
                dosImagenes, precio, LocalDateTime.now());

        // Act & Assert: Las 2 entidades son la misma porque se identificaon con la misma ID
        assertEquals(original, otraVersion);
    }

    @Test
    void noDebeCrearUnArticuloConMenosDeDosImagenes() {
        // Arrange
        List<String> unaSolaImagen = List.of("foto1.jpg");

        // Act & Assert
        assertThrows(ReglaDominioException.class, () -> Articulo.crear(
                artesanoId, "Mochila wayuu", "Tejida en algodón", 5, Grupo.TEJIDOS,
                unaSolaImagen, precio, LocalDateTime.now()));
    }

    @Test
    void alAgotarseElStockElArticuloQuedaAgotado() {
        // Arrange
        Articulo articulo = Articulo.crear(artesanoId, "Vasija de barro", "Cerámica artesanal",
                1, Grupo.CERAMICA, dosImagenes, precio, LocalDateTime.now());

        // Act
        articulo.reducirStock(1);

        // Assert
        assertEquals(EstadoArticulo.AGOTADO, articulo.getEstado());
        assertEquals(0, articulo.getStock());
    }
}
