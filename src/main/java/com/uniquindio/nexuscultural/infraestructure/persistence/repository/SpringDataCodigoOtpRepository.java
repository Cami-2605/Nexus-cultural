package com.uniquindio.nexuscultural.infraestructure.persistence.repository;

import com.uniquindio.nexuscultural.infraestructure.persistence.entity.CodigoOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataCodigoOtpRepository extends JpaRepository<CodigoOtpEntity, UUID> {
    Optional<CodigoOtpEntity> findTopByEmailOrderByFechaCreacionDesc(String email);
}