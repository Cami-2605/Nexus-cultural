package com.uniquindio.nexuscultural.application.usecase;


import com.uniquindio.nexuscultural.application.dto.response.MensajeRespuestaResponse;
import com.uniquindio.nexuscultural.domain.repository.TokenProviderPortRepository;
import org.springframework.stereotype.Service;

// Para cumplimiento ley 27001 proteccion de datos personales
@Service
public class CerrarSesionUseCase {

    private final TokenProviderPortRepository tokenProviderPort;

    public CerrarSesionUseCase(TokenProviderPortRepository tokenProviderPort) {
        this.tokenProviderPort = tokenProviderPort;
    }

    public MensajeRespuestaResponse ejecutar(String tokenHeader) {
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String jwt = tokenHeader.substring(7);
            tokenProviderPort.invalidarToken(jwt);
        }
        return new MensajeRespuestaResponse("Sesión cerrada con éxito.", true);
    }
}
