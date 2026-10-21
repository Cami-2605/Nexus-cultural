package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EliminarAdministradorUseCase {

    private final AdministradorRepository administradorRepository;

    public EliminarAdministradorUseCase(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Transactional
    public void ejecutar(UUID id) {
        administradorRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDominioException("Administrador no encontrado con el ID: " + id));
        administradorRepository.eliminar(id);
    }
}
