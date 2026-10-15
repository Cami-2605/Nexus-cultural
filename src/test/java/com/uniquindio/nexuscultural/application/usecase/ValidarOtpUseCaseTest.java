package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ValidarOtpUseCaseTest {

    private CodigoOtpRepository otpRepository;
    private ValidarOtpUseCase validarOtpUseCase;

    @BeforeEach
    void setUp() {
        otpRepository = Mockito.mock(CodigoOtpRepository.class);
        validarOtpUseCase = new ValidarOtpUseCase(otpRepository);
    }

    @Test
    @DisplayName("Debe validar exitosamente cuando el OTP coincide")
    void debeValidarOtpExitosamente() {
        String email = "juan@nexuscultural.com";
        CodigoOtp otp = CodigoOtp.generar(5);

        when(otpRepository.buscarUltimoPorEmail(email)).thenReturn(Optional.of(otp));

        boolean esValido = validarOtpUseCase.ejecutar(email, otp.codigo());

        assertTrue(esValido);
        verify(otpRepository, times(1)).actualizar(eq(email), any(CodigoOtp.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el código OTP es incorrecto")
    void debeLanzarExcepcionCuandoCodigoEsErroneo() {
        String email = "juan@nexuscultural.com";
        CodigoOtp otp = CodigoOtp.generar(5);

        when(otpRepository.buscarUltimoPorEmail(email)).thenReturn(Optional.of(otp));

        assertThrows(ReglaDominioException.class, () -> validarOtpUseCase.ejecutar(email, "000000"));
    }
}