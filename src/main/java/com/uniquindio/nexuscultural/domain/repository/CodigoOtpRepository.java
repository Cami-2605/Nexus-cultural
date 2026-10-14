package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import java.util.Optional;

public interface CodigoOtpRepository {
    void guardar(String email, CodigoOtp otp);
    Optional<CodigoOtp> buscarUltimoPorEmail(String email);
    void actualizar(String email, CodigoOtp otp);
}