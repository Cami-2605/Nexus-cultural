package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.application.dto.request.AutenticarUsuarioRequest;
import com.uniquindio.nexuscultural.application.dto.response.AuthTokenResponse;
import com.uniquindio.nexuscultural.domain.repository.TokenProviderPortRepository;
import org.springframework.stereotype.Service;

@Service
public class AutenticarUsuarioUseCase {

    private final TokenProviderPortRepository tokenProviderPort;

    public AutenticarUsuarioUseCase(TokenProviderPortRepository tokenProviderPort) {
        this.tokenProviderPort = tokenProviderPort;
    }

    public AuthTokenResponse ejecutar(AutenticarUsuarioRequest request) {
        // TODO: Validar las credenciales contra el UserRepository en PostgreSQL
        String tokenJwt = tokenProviderPort.generarToken(request.usuario(), "ARTESANO");

        return new AuthTokenResponse(tokenJwt, "Bearer", "ARTESANO", 86400);
    }
}
