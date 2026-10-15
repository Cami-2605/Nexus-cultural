package com.uniquindio.nexuscultural.infraestructure.persistence.adapter;

import com.uniquindio.nexuscultural.domain.repository.CodigoOtpRepository;
import com.uniquindio.nexuscultural.domain.valueobject.CodigoOtp;
import com.uniquindio.nexuscultural.infraestructure.persistence.entity.CodigoOtpEntity;
import com.uniquindio.nexuscultural.infraestructure.persistence.repository.SpringDataCodigoOtpRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

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

    @Override
    public void guardar(String email, CodigoOtp otp) {
        CodigoOtpEntity entity = new CodigoOtpEntity();
        entity.setId(UUID.randomUUID());
        entity.setEmail(email);
        entity.setCodigo(otp.codigo());
        entity.setFechaExpiracion(otp.fechaExpiracion());
        entity.setEstado(otp.estado());
        entity.setFechaCreacion(LocalDateTime.now());

        springDataRepository.save(entity);
    }

    @Override
    public Optional<CodigoOtp> buscarUltimoPorEmail(String email) {
        return springDataRepository.findTopByEmailOrderByFechaCreacionDesc(email)
                .map(entity -> new CodigoOtp(
                        entity.getCodigo(),
                        entity.getFechaExpiracion(),
                        entity.getEstado()
                ));
    }

    @Override
    public void actualizar(String email, CodigoOtp otp) {
        springDataRepository.findTopByEmailOrderByFechaCreacionDesc(email)
                .ifPresent(entity -> {
                    entity.setEstado(otp.estado());
                    springDataRepository.save(entity);
                });
    }
}