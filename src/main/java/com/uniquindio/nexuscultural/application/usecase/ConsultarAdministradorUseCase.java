package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ConsultarAdministradorUseCase {

    private final AdministradorRepository administradorRepository;

    public ConsultarAdministradorUseCase(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Transactional(readOnly = true)
    public Administrador ejecutar(UUID id) {
        return administradorRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDominioException("Administrador no encontrado con el ID: " + id));
    }
}
