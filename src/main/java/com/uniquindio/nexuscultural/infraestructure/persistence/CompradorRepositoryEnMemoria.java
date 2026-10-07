package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CompradorRepositoryEnMemoria implements CompradorRepository {

    private final Map<UUID, Comprador> compradores = new HashMap<>();

    @Override
    public Comprador guardar(Comprador comprador) {
        compradores.put(comprador.getId(), comprador);
        return comprador;
    }

    @Override
    public Optional<Comprador> buscarPorId(UUID id) {
        return Optional.ofNullable(compradores.get(id));
    }

    @Override
    public Optional<Comprador> buscarPorEmail(Email email) {
        return compradores.values().stream()
                .filter(c -> c.getEmail().equals(email))
                .findFirst();
    }
}
