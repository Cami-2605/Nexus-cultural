package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
        import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrarArtesanoUseCaseTest {

    private ArtesanoRepository artesanoRepository;
    private RegistrarArtesanoUseCase registrarArtesanoUseCase;

    @BeforeEach
    void setUp() {
        artesanoRepository = Mockito.mock(ArtesanoRepository.class);
        registrarArtesanoUseCase = new RegistrarArtesanoUseCase(artesanoRepository);
    }

    @Test
    @DisplayName("Debe registrar un artesano exitosamente cuando los datos son válidos")
    void debeRegistrarArtesanoExitosamente() {
        // Arrange
        String nombre = "María Carmen";
        String email = "maria@artesanias.com";
        TrayectoriaCultural trayectoria = new TrayectoriaCultural(12, List.of("Premio Maestro Tejedor"));
        UbicacionGeografica ubicacion = new UbicacionGeografica("Quindío", "Salento", "Calle Real #4-12");

        when(artesanoRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(artesanoRepository.guardar(any(Artesano.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Artesano resultado = registrarArtesanoUseCase.ejecutar(nombre, email, trayectoria, ubicacion);

        // Assert
        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombreCompleto());
        assertEquals(email, resultado.getEmail().valor());
        assertTrue(resultado.isActivo());
        verify(artesanoRepository, times(1)).guardar(any(Artesano.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe un artesano registrado con el mismo correo")
    void debeLanzarExcepcionCuandoEmailEstaDuplicado() {
        // Arrange
        String emailDuplicado = "correo@existente.com";
        TrayectoriaCultural trayectoria = new TrayectoriaCultural(5, List.of());
        UbicacionGeografica ubicacion = new UbicacionGeografica("Quindío", "Armenia", "Av. Bolívar");

        Artesano artesanoExistente = Artesano.builder().build();
        when(artesanoRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(artesanoExistente));

        // Act & Assert
        assertThrows(ReglaDominioException.class, () ->
                registrarArtesanoUseCase.ejecutar("Carlos Gómez", emailDuplicado, trayectoria, ubicacion)
        );
        verify(artesanoRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el nombre viene vacío")
    void debeLanzarExcepcionCuandoNombreEstaVacio() {
        TrayectoriaCultural trayectoria = new TrayectoriaCultural(2, List.of());
        UbicacionGeografica ubicacion = new UbicacionGeografica("Quindío", "Filandia", "Cra 6");

        assertThrows(ReglaDominioException.class, () ->
                registrarArtesanoUseCase.ejecutar("   ", "valido@correo.com", trayectoria, ubicacion)
        );
    }
}
