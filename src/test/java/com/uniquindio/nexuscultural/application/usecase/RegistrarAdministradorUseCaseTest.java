package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.NivelAcceso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RegistrarAdministradorUseCaseTest {

    private AdministradorRepository administradorRepository;
    private CompradorRepository compradorRepository;
    private ArtesanoRepository artesanoRepository;
    private RegistrarAdministradorUseCase registrarAdministradorUseCase;

    @BeforeEach
    void setUp() {
        administradorRepository = Mockito.mock(AdministradorRepository.class);
        compradorRepository = Mockito.mock(CompradorRepository.class);
        artesanoRepository = Mockito.mock(ArtesanoRepository.class);

        registrarAdministradorUseCase = new RegistrarAdministradorUseCase(
                administradorRepository,
                compradorRepository,
                artesanoRepository
        );
    }

    @Test
    @DisplayName("Debe registrar un administrador exitosamente cuando los datos son válidos")
    void debeRegistrarAdministradorExitosamente() {
        // Arrange
        String nombre = "Admin General";
        String email = "admin@nexuscultural.com";
        NivelAcceso nivelAcceso = NivelAcceso.SUPER_ADMIN; // Ajusta según tus valores de NivelAcceso

        when(administradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(compradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(artesanoRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(administradorRepository.guardar(any(Administrador.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Administrador resultado = registrarAdministradorUseCase.ejecutar(nombre, email, nivelAcceso);

        // Assert
        assertNotNull(resultado);
        assertEquals(nombre, resultado.getNombreCompleto());
        assertEquals(email, resultado.getEmail().valor());
        assertEquals(nivelAcceso, resultado.getNivelAcceso());
        verify(administradorRepository, times(1)).guardar(any(Administrador.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si ya existe un administrador registrado con el mismo correo")
    void debeLanzarExcepcionCuandoEmailExisteEnAdministradores() {
        // Arrange
        String emailDuplicado = "admin@existente.com";
        Administrador adminExistente = Mockito.mock(Administrador.class);

        when(administradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(adminExistente));

        // Act & Assert
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class, () ->
                registrarAdministradorUseCase.ejecutar("Admin Dos", emailDuplicado, NivelAcceso.SUPER_ADMIN)
        );

        assertTrue(excepcion.getMessage().contains("Ya existe un administrador registrado con el correo"));
        verify(administradorRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el correo ya está registrado por un comprador")
    void debeLanzarExcepcionCuandoEmailExisteEnCompradores() {
        // Arrange
        String emailComprador = "comprador@existente.com";
        Comprador compradorExistente = Mockito.mock(Comprador.class);

        when(administradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(compradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(compradorExistente));

        // Act & Assert
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class, () ->
                registrarAdministradorUseCase.ejecutar("Admin Tres", emailComprador, NivelAcceso.SUPER_ADMIN)
        );

        assertTrue(excepcion.getMessage().contains("Ya existe un comprador registrado con el correo"));
        verify(administradorRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el correo ya está registrado por un artesano")
    void debeLanzarExcepcionCuandoEmailExisteEnArtesanos() {
        // Arrange
        String emailArtesano = "artesano@existente.com";
        Artesano artesanoExistente = Mockito.mock(Artesano.class);

        when(administradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(compradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(artesanoRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(artesanoExistente));

        // Act & Assert
        ReglaDominioException excepcion = assertThrows(ReglaDominioException.class, () ->
                registrarAdministradorUseCase.ejecutar("Admin Cuatro", emailArtesano, NivelAcceso.SUPER_ADMIN)
        );

        assertTrue(excepcion.getMessage().contains("Ya existe un artesano registrado con el correo"));
        verify(administradorRepository, never()).guardar(any());
    }
}