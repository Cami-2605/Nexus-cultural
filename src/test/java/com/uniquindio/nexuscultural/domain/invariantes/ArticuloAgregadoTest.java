package com.uniquindio.nexuscultural.domain.invariantes;

import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

    class ArticuloAgregadoTest {
    
        @Test
        @DisplayName("Invariante: No permite crear Articulo sin mínimo 2 imágenes - Doble Barrera")
        void testInvarianteMinimoImagenesExpositoriasDobleBarrera() {
            // Preparar datos válidos excepto el número de imágenes
            UUID artesanoId = UUID.randomUUID();
            Precio precio = new Precio(new BigDecimal("50000"), "COP");
            List<String> imagenesInsuficientes = List.of("foto1.jpg"); // Solo 1 imagen (violación regla #4)
            String tituloOriginal = "Sombrero Vueltiao";
            String descripcionOriginal = "Tejido tradicional con caña flecha";
            int stockOriginal = 5;
            Grupo grupoOriginal = Grupo.TEJIDOS;
            LocalDateTime ahora = LocalDateTime.now();
    
            // Comprobar que se dispara la excepción del dominio
            assertThrows(ReglaDominioException.class, () -> {
                Articulo.crear(artesanoId, tituloOriginal, descripcionOriginal,
                        stockOriginal, grupoOriginal, imagenesInsuficientes, precio, ahora);
            });
    
            // Verificar que no se creó ningún Articulo en estado inconsistente
            // Al ser la creación fallida, no existe instancia del Agregado Articulo
        }
    
        @Test
        @DisplayName("Invariante: No permite crear Articulo sin Grupo cultural - Doble Barrera")
        void testInvarianteGrupoCulturalObligatorioDobleBarrera() {
            // Preparar datos válidos excepto el grupo (violación regla #2)
            UUID artesanoId = UUID.randomUUID();
            Precio precio = new Precio(new BigDecimal("75000"), "COP");
            List<String> imagenesValidas = List.of("foto1.jpg", "foto2.jpg");
            String tituloOriginal = "Mochila Wayuu";
            String descripcionOriginal = "Tejido en crochet con diseños ancestrales";
            int stockOriginal = 3;
            LocalDateTime ahora = LocalDateTime.now();
    
            // Comprobar que se dispara la excepción del dominio
            assertThrows(ReglaDominioException.class, () -> {
                Articulo.crear(artesanoId, tituloOriginal, descripcionOriginal,
                        stockOriginal, null, imagenesValidas, precio, ahora);
            });
    
            // Verificar que no se creó ningún Articulo sin grupo
            // La creación fallida garantiza que no existen Articulos sin categoría cultural
        }
    
        @Test
        @DisplayName("Invariante: No permite actualizar descripción con valor vacío - Doble Barrera")
        void testInvarianteDescripcionNoVaciaDobleBarrera() {
            // Crear Articulo válido y capturar estado original
            UUID artesanoId = UUID.randomUUID();
            Precio precio = new Precio(new BigDecimal("45000"), "COP");
            List<String> imagenesValidas = List.of("foto1.jpg", "foto2.jpg");
            String descripcionOriginal = "Cerámica modelada a mano con técnicas precolombinas";
    
            Articulo articulo = Articulo.crear(
                    artesanoId,
                    "Vasija Quimbaya",
                    descripcionOriginal,
                    4,
                    Grupo.CERAMICA,
                    imagenesValidas,
                    precio,
                    LocalDateTime.now()
            );
    
            // Intentar actualizar con descripción vacía
            assertThrows(ReglaDominioException.class, () -> {
                articulo.actualizarDescripcion(""); // Violación de regla #1 (historia cultural)
            });
    
            // Verificar que la descripción original permaneció intacta
            assertEquals(descripcionOriginal, articulo.getDescripcion(),
                    "La descripción del Articulo debe permanecer intacta tras el intento de actualización inválida");
        }
    }
