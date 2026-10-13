package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.repository.TokenProviderPortRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenProviderAdapterEnMemoria implements TokenProviderPortRepository {

    @Override
    public String generarToken(String usuarioId, String rol) {
        // Genera un token ficticio para pruebas locales
        return "fake-jwt-token-" + rol.toLowerCase() + "-" + UUID.randomUUID();
    }

    @Override
    public void invalidarToken(String token) {
        // Simulación de invalidación de token
    }
}