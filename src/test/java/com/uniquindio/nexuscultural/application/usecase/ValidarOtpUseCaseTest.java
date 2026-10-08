package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.ValidarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.response.MensajeRespuestaResponse;
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
        String telefono = "3001234567";
        CodigoOtp otp = CodigoOtp.generar(5);
        ValidarOtpRequest request = new ValidarOtpRequest(telefono, otp.codigo());

        when(otpRepository.buscarUltimoPorTelefono(telefono)).thenReturn(Optional.of(otp));

        MensajeRespuestaResponse respuesta = validarOtpUseCase.ejecutar(request);

        assertTrue(respuesta.exitoso());
        assertEquals("Verificación exitosa.", respuesta.mensaje());
        verify(otpRepository, times(1)).actualizar(eq(telefono), any(CodigoOtp.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el código OTP es incorrecto")
    void debeLanzarExcepcionCuandoCodigoEsErroneo() {
        String telefono = "3001234567";
        CodigoOtp otp = CodigoOtp.generar(5);
        ValidarOtpRequest request = new ValidarOtpRequest(telefono, "000000");

        when(otpRepository.buscarUltimoPorTelefono(telefono)).thenReturn(Optional.of(otp));

        assertThrows(ReglaDominioException.class, () -> validarOtpUseCase.ejecutar(request));
    }
}