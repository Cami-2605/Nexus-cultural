package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Para postgrest esto debe estar desactivado @Repository activado
public class CodigoOtpRepositoryEnMemoria implements CodigoOtpRepository {

    private final Map<String, CodigoOtp> codigosOtp = new ConcurrentHashMap<>();

    @Override
    public void guardar(String email, CodigoOtp otp) {
        codigosOtp.put(email, otp);
    }

    @Override
    public Optional<CodigoOtp> buscarUltimoPorEmail(String email) {
        return Optional.ofNullable(codigosOtp.get(email));
    }

    @Override
    public void actualizar(String email, CodigoOtp otp) {
        codigosOtp.put(email, otp);
    }
}