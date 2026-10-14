package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.NivelAcceso;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CambiarNivelAccesoAdministradorUseCase {

    private final AdministradorRepository administradorRepository;

    public CambiarNivelAccesoAdministradorUseCase(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Transactional
    public Administrador ejecutar(UUID id, NivelAcceso nuevoNivelAcceso) {
        Administrador admin = administradorRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDominioException("Administrador no encontrado con el ID: " + id));

        admin.cambiarNivelAcceso(nuevoNivelAcceso);
        return administradorRepository.guardar(admin);
    }
}
