package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrarCompradorUseCaseTest {

    private CompradorRepository compradorRepository;
    private ArtesanoRepository artesanoRepository;
    private AdministradorRepository administradorRepository;
    private RegistrarCompradorUseCase registrarCompradorUseCase;

    @BeforeEach
    void setUp() {
        compradorRepository = Mockito.mock(CompradorRepository.class);
        artesanoRepository = Mockito.mock(ArtesanoRepository.class);
        administradorRepository = Mockito.mock(AdministradorRepository.class);

        registrarCompradorUseCase = new RegistrarCompradorUseCase(
                compradorRepository,
                artesanoRepository,
                administradorRepository
        );
    }

    @Test
    @DisplayName("Debe registrar un comprador exitosamente cuando los datos son válidos")
    void debeRegistrarCompradorExitosamente() {
        // Arrange
        String nombre = "Carlos Mendoza";
        String email = "carlos@gmail.com";
        UbicacionGeografica direccion = new UbicacionGeografica("Quindío", "Armenia", "Cra 14 #10-20");

        when(compradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(artesanoRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(administradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(compradorRepository.guardar(any(Comprador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Comprador resultado = registrarCompradorUseCase.ejecutar(nombre, email, direccion);

        // Assert
        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombreCompleto());
        assertEquals(email, resultado.getEmail().valor());
        verify(compradorRepository, times(1)).guardar(any(Comprador.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe un comprador registrado con el mismo correo")
    void debeLanzarExcepcionCuandoEmailEstaDuplicadoEnCompradores() {
        // Arrange
        String emailDuplicado = "comprador@existente.com";
        UbicacionGeografica direccion = new UbicacionGeografica("Quindío", "Calarcá", "Calle 5 #8-12");

        Comprador compradorExistente = Mockito.mock(Comprador.class);
        when(compradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(compradorExistente));

        // Act & Assert
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class, () ->
                registrarCompradorUseCase.ejecutar("Juan Pérez", emailDuplicado, direccion)
        );

        assertTrue(excepcion.getMessage().contains("Ya existe un comprador registrado con el correo"));
        verify(compradorRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el correo ya está registrado por un artesano")
    void debeLanzarExcepcionCuandoEmailExisteEnArtesanos() {
        // Arrange
        String emailArtesano = "artesano@existente.com";
        UbicacionGeografica direccion = new UbicacionGeografica("Quindío", "Salento", "Calle Real");

        Artesano artesanoExistente = Mockito.mock(Artesano.class);
        when(compradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(artesanoRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(artesanoExistente));

        // Act & Assert
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class, () ->
                registrarCompradorUseCase.ejecutar("Laura Gómez", emailArtesano, direccion)
        );

        assertTrue(excepcion.getMessage().contains("Ya existe un artesano registrado con el correo"));
        verify(compradorRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el correo ya está registrado por un administrador")
    void debeLanzarExcepcionCuandoEmailExisteEnAdministradores() {
        // Arrange
        String emailAdmin = "admin@nexuscultural.com";
        UbicacionGeografica direccion = new UbicacionGeografica("Quindío", "Armenia", "Av. Bolívar");

        Administrador adminExistente = Mockito.mock(Administrador.class);
        when(compradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(artesanoRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(administradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(adminExistente));

        // Act & Assert
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class, () ->
                registrarCompradorUseCase.ejecutar("Admin General", emailAdmin, direccion)
        );

        assertTrue(excepcion.getMessage().contains("Ya existe un administrador registrado con el correo"));
        verify(compradorRepository, never()).guardar(any());
    }

}