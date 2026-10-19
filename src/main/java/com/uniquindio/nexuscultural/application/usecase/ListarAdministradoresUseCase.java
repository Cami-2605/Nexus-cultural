package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListarAdministradoresUseCase {

    private final AdministradorRepository administradorRepository;

    public ListarAdministradoresUseCase(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Transactional(readOnly = true)
    public List<Administrador> ejecutar() {
        return administradorRepository.listarTodos();
    }
}
