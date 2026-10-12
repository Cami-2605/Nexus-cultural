package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import java.util.Optional;

public interface CodigoOtpRepository {
    void guardar(String telefono, CodigoOtp otp);
    Optional<CodigoOtp> buscarUltimoPorTelefono(String telefono);
    void actualizar(String telefono, CodigoOtp otp);
}