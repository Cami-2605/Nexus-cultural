package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.SolicitarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.response.MensajeRespuestaResponse;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import com.uniquindio.nexuscultural.infraestructure.persistence.adapter.FirebaseNotificadorAdapter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitarOtpUseCaseTest {

    @Mock
    private CodigoOtpRepository otpRepository;

    @Mock
    private FirebaseNotificadorAdapter firebaseNotificadorAdapter;

    @InjectMocks
    private SolicitarOtpUseCase solicitarOtpUseCase;

    @Test
    @DisplayName("Debe generar OTP, guardar en repositorio y enviar notificación por correo")
    void solicitarOtpExitoso() {
        String email = "usuario@correo.com";
        SolicitarOtpRequest request = new SolicitarOtpRequest(email);

        doNothing().when(otpRepository).guardar(eq(email), any(CodigoOtp.class));
        doNothing().when(firebaseNotificadorAdapter).enviarCorreoOtp(eq(email), anyString());

        MensajeRespuestaResponse response = solicitarOtpUseCase.ejecutar(request);

        assertNotNull(response);
        assertTrue(response.exitoso());
        assertTrue(response.mensaje().contains("éxito"));

        verify(otpRepository, times(1)).guardar(eq(email), any(CodigoOtp.class));
        verify(firebaseNotificadorAdapter, times(1)).enviarCorreoOtp(eq(email), anyString());
    }
}

