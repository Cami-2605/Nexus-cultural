package com.uniquindio.nexuscultural.application.usecase;

import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.valueobject.Email;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RegistrarCompradorUseCase {

    private final CompradorRepository compradorRepository;

    public RegistrarCompradorUseCase(CompradorRepository compradorRepository) {
        this.compradorRepository = compradorRepository;
    }

    public Comprador ejecutar(String nombreCompleto, String emailTexto, UbicacionGeografica direccion) {
        Email email = new Email(emailTexto);

        // Delegamos la verificación/validación al repositorio o servicio de dominio
        compradorRepository.buscarPorEmail(email).ifPresent(a -> {
            throw new ReglaDominioException("Ya existe un cliente registrado con el correo: " + emailTexto);
        });

        Comprador nuevoComprador = Comprador.registrar(nombreCompleto, email, direccion, LocalDateTime.now());
        return compradorRepository.guardar(nuevoComprador);
    }
}