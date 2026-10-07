package com.uniquindio.nexuscultural.domain.repository;

import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.valueobject.Email;

import java.util.Optional;
import java.util.UUID;

public interface ArtesanoRepository {

    Artesano guardar(Artesano artesano);

    Optional<Artesano> buscarPorId(UUID id);

    Optional<Artesano> buscarPorEmail(Email email);
}