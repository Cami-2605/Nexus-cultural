package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarDireccionRequest;
import com.uniquindio.nexuscultural.application.dto.request.ActualizarPerfilCompradorRequest;
import com.uniquindio.nexuscultural.application.dto.request.RegistrarCompradorRequest;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.infraestructure.persistence.CompradorRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CompradorServiceTest {

    private CompradorRepositoryEnMemoria compradorRepository;
    private CompradorService compradorService;

    @BeforeEach
    void setUp() {
        compradorRepository = new CompradorRepositoryEnMemoria();
        compradorService = new CompradorService(compradorRepository);
    }

    @Test
    @DisplayName("Debe registrar un comprador exitosamente cuando los datos son válidos")
    void registrarCompradorExitoso() {
        // Arrange
        RegistrarCompradorRequest request = new RegistrarCompradorRequest(
                "Willy Suarez",
                "widzu@correo.com",
                "Quindío",
                "Armenia",
                "Calle 10 # 14-20"
        );

        // Act
        Comprador resultado = compradorService.registrarComprador(request);

        // Assert
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("Willy Suarez", resultado.getNombreCompleto());
        assertEquals("widzu@correo.com", resultado.getEmail().valor());
        assertEquals("Quindío", resultado.getDireccionEntrega().departamento());
        assertTrue(resultado.getFavoritos().isEmpty());
    }

    @Test
    @DisplayName("Debe lanzar ReglaDominioException al intentar registrar un comprador con correo duplicado")
    void registrarCompradorCorreoDuplicado() {
        // Arrange
        RegistrarCompradorRequest request1 = new RegistrarCompradorRequest(
                "Willy Zapata",
                "duplicado@correo.com",
                "Quindío",
                "Armenia",
                "Calle 10"
        );
        compradorService.registrarComprador(request1);

        RegistrarCompradorRequest request2 = new RegistrarCompradorRequest(
                "Otro Usuario",
                "duplicado@correo.com",
                "Quindío",
                "Calarcá",
                "Carrera 25"
        );

        // Act & Assert
        ReglaDominioException exception = assertThrows(
                ReglaDominioException.class,
                () -> compradorService.registrarComprador(request2)
        );

        assertTrue(exception.getMessage().contains("Ya existe un comprador registrado con el correo"));
    }

    @Test
    @DisplayName("Debe agregar un artículo a favoritos correctamente")
    void agregarFavoritoExitoso() {
        // Arrange
        Comprador comprador = registrarCompradorDePrueba();
        UUID articuloId = UUID.randomUUID();

        // Act
        Comprador actualizado = compradorService.agregarFavorito(comprador.getId(), articuloId);

        // Assert
        assertEquals(1, actualizado.getFavoritos().size());
        assertTrue(actualizado.getFavoritos().contains(articuloId));
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta superar el límite de 15 favoritos")
    void agregarFavoritoExcedeLimite() {
        // Arrange
        Comprador comprador = registrarCompradorDePrueba();

        // Agregamos 15 favoritos previamente
        for (int i = 0; i < 15; i++) {
            compradorService.agregarFavorito(comprador.getId(), UUID.randomUUID());
        }

        UUID articulo16 = UUID.randomUUID();

        // Act & Assert: El piso de validación lo hace la Entidad Comprador al ejecutar la regla
        ReglaDominioException exception = assertThrows(
                ReglaDominioException.class,
                () -> compradorService.agregarFavorito(comprador.getId(), articulo16)
        );

        assertTrue(exception.getMessage().contains("no puede tener más de 15 artículos favoritos"));
    }

    @Test
    @DisplayName("Debe actualizar el perfil del comprador")
    void actualizarPerfilExitoso() {
        // Arrange
        Comprador comprador = registrarCompradorDePrueba();
        ActualizarPerfilCompradorRequest request = new ActualizarPerfilCompradorRequest("Willy Suarez");

        // Act
        Comprador actualizado = compradorService.actualizarPerfil(comprador.getId(), request);

        // Assert
        assertEquals("Willy Suarez", actualizado.getNombreCompleto());
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar un comprador con ID inexistente")
    void obtenerPorIdNoEncontrado() {
        // Arrange
        UUID idInexistente = UUID.randomUUID();

        // Act & Assert
        assertThrows(
                ReglaDominioException.class,
                () -> compradorService.obtenerPorId(idInexistente)
        );
    }

    // Helper method para reutilizar la creación de un comprador básico en los tests
    private Comprador registrarCompradorDePrueba() {
        RegistrarCompradorRequest request = new RegistrarCompradorRequest(
                "Comprador Test",
                "test" + UUID.randomUUID() + "@correo.com",
                "Quindío",
                "Armenia",
                "Calle 12"
        );
        return compradorService.registrarComprador(request);
    }
}
