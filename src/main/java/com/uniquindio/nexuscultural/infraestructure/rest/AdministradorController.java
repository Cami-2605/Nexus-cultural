package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.usecase.CambiarNivelAccesoAdministradorUseCase;
import com.uniquindio.nexuscultural.application.usecase.ConsultarAdministradorUseCase;
import com.uniquindio.nexuscultural.application.usecase.RegistrarAdministradorUseCase;
import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.application.dto.request.CambiarNivelAccesoRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearAdministradorRequest;
import com.uniquindio.nexuscultural.application.dto.response.AdministradorResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

        import java.util.UUID;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    private final RegistrarAdministradorUseCase registrarAdministradorUseCase;
    private final ConsultarAdministradorUseCase consultarAdministradorUseCase;
    private final CambiarNivelAccesoAdministradorUseCase cambiarNivelAccesoAdministradorUseCase;

    public AdministradorController(RegistrarAdministradorUseCase registrarAdministradorUseCase,
                                   ConsultarAdministradorUseCase consultarAdministradorUseCase,
                                   CambiarNivelAccesoAdministradorUseCase cambiarNivelAccesoAdministradorUseCase) {
        this.registrarAdministradorUseCase = registrarAdministradorUseCase;
        this.consultarAdministradorUseCase = consultarAdministradorUseCase;
        this.cambiarNivelAccesoAdministradorUseCase = cambiarNivelAccesoAdministradorUseCase;
    }

    // POST /administradores
    @PostMapping
    public ResponseEntity<AdministradorResponse> registrar(@Valid @RequestBody CrearAdministradorRequest request) {
        Administrador admin = registrarAdministradorUseCase.ejecutar(
                request.nombreCompleto(),
                request.email(),
                request.nivelAcceso()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(AdministradorResponse.desdeDominio(admin));
    }

    // GET /administradores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> obtenerPorId(@PathVariable UUID id) {
        Administrador admin = consultarAdministradorUseCase.ejecutar(id);
        return ResponseEntity.ok(AdministradorResponse.desdeDominio(admin));
    }

    // PUT /administradores/{id}/nivel_acceso
    @PutMapping("/{id}/nivel_acceso")
    public ResponseEntity<AdministradorResponse> cambiarNivelAcceso(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarNivelAccesoRequest request) {
        Administrador admin = cambiarNivelAccesoAdministradorUseCase.ejecutar(id, request.nivelAcceso());
        return ResponseEntity.ok(AdministradorResponse.desdeDominio(admin));
    }
}