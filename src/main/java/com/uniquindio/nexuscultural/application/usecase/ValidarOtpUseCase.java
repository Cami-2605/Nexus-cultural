package com.uniquindio.nexuscultural.application.usecase;


import com.uniquindio.nexuscultural.application.dto.request.ValidarOtpRequest;
import com.uniquindio.nexuscultural.application.dto.response.MensajeRespuestaResponse;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import org.springframework.stereotype.Service;

@Service
public class ValidarOtpUseCase {

    private final CodigoOtpRepository otpRepository;

    public ValidarOtpUseCase(CodigoOtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public MensajeRespuestaResponse ejecutar(ValidarOtpRequest request) {
        // Arrange: Recuperar estado actual desde el repositorio
        CodigoOtp otp = otpRepository.buscarUltimoPorTelefono(request.telefono())
                .orElseThrow(() -> new ReglaDominioException("No existe un código OTP solicitado para este número."));

        // Act: El dominio evalúa si es válido
        if (!otp.esValido(request.codigo())) {
            throw new ReglaDominioException("El código OTP es incorrecto o ha expirado.");
        }

        // Assert/Save: Se actualiza el estado inmutablemente a USADO
        otpRepository.actualizar(request.telefono(), otp.marcarComoUsado());

        return new MensajeRespuestaResponse("Verificación exitosa.", true);
    }
}