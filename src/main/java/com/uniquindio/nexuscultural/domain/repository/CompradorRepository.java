package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.valueobject.Email;

import java.util.Optional;
import java.util.UUID;

public interface CompradorRepository {

    Comprador guardar(Comprador comprador);

    Optional<Comprador> buscarPorId(UUID id);

    Optional<Comprador> buscarPorEmail(Email email);
}
