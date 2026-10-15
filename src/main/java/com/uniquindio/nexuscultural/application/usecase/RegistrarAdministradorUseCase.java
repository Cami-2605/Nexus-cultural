package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.AdministradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.NivelAcceso;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RegistrarAdministradorUseCase {

    private final AdministradorRepository administradorRepository;

    public RegistrarAdministradorUseCase(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Transactional
    public Administrador ejecutar(String nombreCompleto, String emailTexto, NivelAcceso nivelAcceso) {
        Email email = new Email(emailTexto);

        administradorRepository.buscarPorEmail(email).ifPresent(a -> {
            throw new ReglaDominioException("Ya existe un administrador registrado con el correo: " + emailTexto);
        });

        Administrador nuevoAdmin = Administrador.builder()
                .id(java.util.UUID.randomUUID())
                .nombreCompleto(nombreCompleto)
                .email(email)
                .fechaRegistro(LocalDateTime.now())
                .nivelAcceso(nivelAcceso)
                .build();

        return administradorRepository.guardar(nuevoAdmin);
    }
}