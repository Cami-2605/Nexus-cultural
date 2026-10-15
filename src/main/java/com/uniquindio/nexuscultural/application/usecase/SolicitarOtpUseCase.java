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
    // Si manejas envío por correo, aquí iría tu interfaz de servicio de correo (ej. PuertoEmail)

    public SolicitarOtpUseCase(CodigoOtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    //Se asigna un valor entero que representa el tiempo en minutos de validez del código
    public MensajeRespuestaResponse ejecutar(SolicitarOtpRequest request) {
        CodigoOtp nuevoOtp = CodigoOtp.generar(5);

        // Se guarda asociado al email
        otpRepository.guardar(request.email(), nuevoOtp);

        System.out.println("📩 [OTP GENERADO]: El código para " + request.email() + " es: " + nuevoOtp.codigo());

        return new MensajeRespuestaResponse("Código OTP enviado con éxito al correo.", true);
    }
}
