package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class AdministradorRepositoryEnMemoria implements AdministradorRepository {

    private final Map<UUID, Administrador> administradores = new HashMap<>();

    @Override
    public Administrador guardar(Administrador administrador) {
        administradores.put(administrador.getId(), administrador);
        return administrador;
    }

    @Override
    public Optional<Administrador> buscarPorId(UUID id) {
        return Optional.ofNullable(administradores.get(id));
    }

    @Override
    public Optional<Administrador> buscarPorEmail(Email email) {
        return administradores.values().stream()
                .filter(a -> a.getEmail().equals(email))
                .findFirst();
    }
}
