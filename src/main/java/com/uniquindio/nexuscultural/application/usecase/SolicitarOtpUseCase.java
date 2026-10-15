package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.SolicitarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.response.MensajeRespuestaResponse;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import com.uniquindio.nexuscultural.infraestructure.persistence.adapter.FirebaseNotificadorAdapter;
import org.springframework.stereotype.Service;

@Service
public class SolicitarOtpUseCase {

    private final CodigoOtpRepository otpRepository;
    private final FirebaseNotificadorAdapter firebaseNotificadorAdapter;

    public SolicitarOtpUseCase(CodigoOtpRepository otpRepository,
                               FirebaseNotificadorAdapter firebaseNotificadorAdapter) {
        this.otpRepository = otpRepository;
        this.firebaseNotificadorAdapter = firebaseNotificadorAdapter;
    }

    public MensajeRespuestaResponse ejecutar(SolicitarOtpRequest request) {
        // Se asigna un valor entero que representa los minutos de validez
        CodigoOtp nuevoOtp = CodigoOtp.generar(5);

        // Se guarda asociado al email en PostgreSQL
        otpRepository.guardar(request.email(), nuevoOtp);

        // Se inserta en la colección "mail" de Firestore para activar Trigger Email
        firebaseNotificadorAdapter.enviarCorreoOtp(request.email(), nuevoOtp.codigo());

        System.out.println("📩 [OTP GENERADO]: El código para " + request.email() + " es: " + nuevoOtp.codigo());

        return new MensajeRespuestaResponse("Código OTP enviado con éxito al correo.", true);
    }
}