package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.CodigoOtpEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.repository.SpringDataCodigoOtpRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class CodigoOtpRepositoryPostgresAdapter implements CodigoOtpRepository {

    private final SpringDataCodigoOtpRepository springDataRepository;

    public CodigoOtpRepositoryPostgresAdapter(SpringDataCodigoOtpRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    public static String hashearCodigo(String codigoPlano) {
        if (codigoPlano == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(codigoPlano.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculando hash SHA-256 de OTP", e);
        }
    }

    @Override
    public void guardar(String email, CodigoOtp otp) {
        CodigoOtpEntity entity = new CodigoOtpEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail(email);
        entity.setCodigoHash(hashearCodigo(otp.codigo()));
        entity.setFechaExpiracion(otp.fechaExpiracion());
        entity.setEstado(otp.estado());
        entity.setFechaCreacion(LocalDateTime.now());
        entity.setIntentosFallidos(otp.intentosFallidos());

        springDataRepository.save(entity);
    }

    @Override
    public Optional<CodigoOtp> buscarUltimoPorEmail(String email) {
        return springDataRepository.findTopByEmailOrderByFechaCreacionDesc(email)
                .map(entity -> new CodigoOtp(
                        entity.getCodigoHash(),
                        entity.getFechaExpiracion(),
                        entity.getEstado(),
                        entity.getIntentosFallidos()
                ));
    }

    @Override
    public void actualizar(String email, CodigoOtp otp) {
        springDataRepository.findTopByEmailOrderByFechaCreacionDesc(email)
                .ifPresent(entity -> {
                    entity.setEstado(otp.estado());
                    entity.setIntentosFallidos(otp.intentosFallidos());
                    springDataRepository.save(entity);
                });
    }
}