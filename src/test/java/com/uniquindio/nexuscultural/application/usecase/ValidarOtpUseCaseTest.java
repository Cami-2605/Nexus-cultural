package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.response.AuthTokenResponse;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoOtp;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import com.uniquindio.nexuscultural.infraestructure.persistence.adapter.CodigoOtpRepositoryPostgresAdapter;
import com.uniquindio.nexuscultural.infraestructure.security.JwtTokenProviderAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidarOtpUseCaseTest {

    @Mock
    private CodigoOtpRepository otpRepository;

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private ArtesanoRepository artesanoRepository;

    @Mock
    private CompradorRepository compradorRepository;

    @Mock
    private JwtTokenProviderAdapter jwtTokenProviderAdapter;

    @InjectMocks
    private ValidarOtpUseCase validarOtpUseCase;

    private String email;
    private String codigoPlano;
    private String codigoHash;
    private CodigoOtp otpMock;
    private Comprador compradorMock;

    @BeforeEach
    void setUp() {
        email = "test@correo.com";
        codigoPlano = "123456";
        codigoHash = CodigoOtpRepositoryPostgresAdapter.hashearCodigo(codigoPlano);

        otpMock = new CodigoOtp(codigoHash, LocalDateTime.now().plusMinutes(5), EstadoOtp.PENDIENTE, 0);

        compradorMock = Comprador.registrar(
                "Usuario Comprador",
                new Email(email),
                new UbicacionGeografica("Quindío", "Armenia", "Calle 1"),
                LocalDateTime.now()
        );

        // Reiniciar contador de intentos estático en caso de pruebas previas
        ValidarOtpUseCase.reiniciarIntentos(email);
    }

    @Test
    @DisplayName("Debe validar OTP exitosamente y retornar token JWT")
    void validarOtpExitoso() {
        when(otpRepository.buscarUltimoPorEmail(email)).thenReturn(Optional.of(otpMock));
        doNothing().when(otpRepository).actualizar(eq(email), any(CodigoOtp.class));

        when(administradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(artesanoRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.empty());
        when(compradorRepository.buscarPorEmail(any(Email.class))).thenReturn(Optional.of(compradorMock));

        when(jwtTokenProviderAdapter.generarToken(any(UUID.class), eq(email), eq("COMPRADOR"), isNull(), anyString()))
                .thenReturn(new AuthTokenResponse("mocked.jwt.token", "Bearer", "COMPRADOR", 3600L));

        AuthTokenResponse response = validarOtpUseCase.ejecutar(email, codigoPlano);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.token());
        assertEquals("COMPRADOR", response.rol());
        verify(otpRepository, times(1)).actualizar(eq(email), any(CodigoOtp.class));
    }

    @Test
    @DisplayName("Debe fallar e incrementar intentos si el código OTP es incorrecto")
    void validarOtpCodigoIncorrecto() {
        when(otpRepository.buscarUltimoPorEmail(email)).thenReturn(Optional.of(otpMock));

        ReglaDominioException ex = assertThrows(
                ReglaDominioException.class,
                () -> validarOtpUseCase.ejecutar(email, "999999")
        );

        assertTrue(ex.getMessage().contains("El código OTP es incorrecto. Intentos restantes: 2"));
    }

    @Test
    @DisplayName("Debe bloquear y lanzar excepción al alcanzar 3 intentos fallidos")
    void validarOtpBloqueoAlTercerIntento() {
        when(otpRepository.buscarUltimoPorEmail(email)).thenReturn(Optional.of(otpMock));

        // Intento 1
        assertThrows(ReglaDominioException.class, () -> validarOtpUseCase.ejecutar(email, "000001"));
        // Intento 2
        assertThrows(ReglaDominioException.class, () -> validarOtpUseCase.ejecutar(email, "000002"));
        // Intento 3: debe bloquear
        ReglaDominioException ex = assertThrows(ReglaDominioException.class, () -> validarOtpUseCase.ejecutar(email, "000003"));

        assertTrue(ex.getMessage().contains("Ha superado el límite de 3 intentos y el código ha sido bloqueado"));
    }

    @Test
    @DisplayName("Debe fallar si el código OTP ya expiró")
    void validarOtpExpirado() {
        CodigoOtp otpExpirado = new CodigoOtp(codigoHash, LocalDateTime.now().minusMinutes(1), EstadoOtp.PENDIENTE, 0);
        when(otpRepository.buscarUltimoPorEmail(email)).thenReturn(Optional.of(otpExpirado));

        ReglaDominioException ex = assertThrows(
                ReglaDominioException.class,
                () -> validarOtpUseCase.ejecutar(email, codigoPlano)
        );

        assertTrue(ex.getMessage().contains("ha expirado"));
    }
}

