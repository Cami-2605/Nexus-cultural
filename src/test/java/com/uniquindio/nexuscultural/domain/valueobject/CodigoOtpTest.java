package com.uniquindio.nexuscultural.domain.valueobject;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CodigoOtpTest {

    @Test
    @DisplayName("Debe generar un OTP válido de 6 dígitos con estado PENDIENTE")
    void debeGenerarOtpValido() {
        CodigoOtp otp = CodigoOtp.generar(5);

        assertNotNull(otp);
        assertEquals(6, otp.codigo().length());
        assertFalse(otp.estaExpirado());
        assertEquals(EstadoOtp.PENDIENTE, otp.estado());
    }

    @Test
    @DisplayName("No debe permitir crear un OTP con formato inválido")
    void noDebeCrearOtpConFormatoInvalido() {
        assertThrows(ReglaDominioException.class, () ->
                new CodigoOtp("ABCDEF", LocalDateTime.now().plusMinutes(5), EstadoOtp.PENDIENTE)
        );
        assertThrows(ReglaDominioException.class, () ->
                new CodigoOtp("12345", LocalDateTime.now().plusMinutes(5), EstadoOtp.PENDIENTE)
        );
    }

    @Test
    @DisplayName("Debe validar exitosamente cuando el código coincide y está vigente")
    void debeValidarCodigoCorrecto() {
        CodigoOtp otp = CodigoOtp.generar(5);

        assertTrue(otp.esValido(otp.codigo()));
    }

    @Test
    @DisplayName("Debe marcar inmutablemente el OTP como USADO")
    void debeMarcarComoUsado() {
        CodigoOtp otp = CodigoOtp.generar(5);
        CodigoOtp otpUsado = otp.marcarComoUsado();

        assertEquals(EstadoOtp.USADO, otpUsado.estado());
        assertFalse(otpUsado.esValido(otp.codigo()));
    }
}