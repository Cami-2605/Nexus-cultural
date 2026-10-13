package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp; // O el paquete correcto de tu CodigoOtp
import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CodigoOtpRepositoryEnMemoria implements CodigoOtpRepository {

    private final Map<String, CodigoOtp> codigosOtp = new ConcurrentHashMap<>();

    @Override
    public void guardar(String telefono, CodigoOtp otp) {
        codigosOtp.put(telefono, otp);
    }

    @Override
    public Optional<CodigoOtp> buscarUltimoPorTelefono(String telefono) {
        return Optional.ofNullable(codigosOtp.get(telefono));
    }

    @Override
    public void actualizar(String telefono, CodigoOtp otp) {
        codigosOtp.put(telefono, otp);
    }
}