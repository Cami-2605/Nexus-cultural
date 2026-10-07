package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.valueobject.Email;

import java.util.Optional;
import java.util.UUID;

public interface AdministradorRepository {

    Administrador guardar(Administrador administrador);

    Optional<Administrador> buscarPorId(UUID id);

    Optional<Administrador> buscarPorEmail(Email email);
}
