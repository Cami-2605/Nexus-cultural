package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.SolicitarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.response.MensajeRespuestaResponse;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.repository.PuertoSMS;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import org.springframework.stereotype.Service;

@Service
public class SolicitarOtpUseCase {

    private final CodigoOtpRepository otpRepository;
    private final PuertoSMS puertoSMS;

    public SolicitarOtpUseCase(CodigoOtpRepository otpRepository, PuertoSMS puertoSMS) {
        this.otpRepository = otpRepository;
        this.puertoSMS = puertoSMS;
    }

    public MensajeRespuestaResponse ejecutar(SolicitarOtpRequest request) {
        // AAA - Arrange & Act: Creación del Value Object del dominio
        CodigoOtp nuevoOtp = CodigoOtp.generar(5);

        // AAA - Assert / Save: Persistencia y despacho del SMS
        otpRepository.guardar(request.telefono(), nuevoOtp);

        String mensaje = "Nexus Cultural: Su código de verificación es " + nuevoOtp.codigo();
        puertoSMS.enviarMensajeSms(request.telefono(), mensaje);

        return new MensajeRespuestaResponse("Código OTP enviado con éxito.", true);
    }
}
