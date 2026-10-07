package com.uniquindio.nexuscultural.infraestructure.persistence;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ArtesanoRepositoryEnMemoria implements ArtesanoRepository {

    private final Map<UUID, Artesano> artesanos = new HashMap<>();

    @Override
    public Artesano guardar(Artesano artesano) {
        artesanos.put(artesano.getId(), artesano);
        return artesano;
    }

    @Override
    public Optional<Artesano> buscarPorId(UUID id) {
        return Optional.ofNullable(artesanos.get(id));
    }

    @Override
    public Optional<Artesano> buscarPorEmail(Email email) {
        return artesanos.values().stream()
                .filter(a -> a.getEmail().equals(email))
                .findFirst();
    }
}
