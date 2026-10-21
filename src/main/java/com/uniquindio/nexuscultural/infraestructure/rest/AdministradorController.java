package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.usecase.CambiarNivelAccesoAdministradorUseCase;
import com.uniquindio.nexuscultural.application.usecase.ConsultarAdministradorUseCase;
import com.uniquindio.nexuscultural.application.usecase.RegistrarAdministradorUseCase;
import com.uniquindio.nexuscultural.domain.entity.Administrador;
import com.uniquindio.nexuscultural.application.dto.request.ActualizarAdministradorRequest;
import com.uniquindio.nexuscultural.application.dto.request.CambiarNivelAccesoRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearAdministradorRequest;
import com.uniquindio.nexuscultural.application.dto.response.AdministradorResponse;
import com.uniquindio.nexuscultural.application.usecase.ActualizarAdministradorUseCase;
import com.uniquindio.nexuscultural.application.usecase.CambiarNivelAccesoAdministradorUseCase;
import com.uniquindio.nexuscultural.application.usecase.ConsultarAdministradorUseCase;
import com.uniquindio.nexuscultural.application.usecase.EliminarAdministradorUseCase;
import com.uniquindio.nexuscultural.application.usecase.ListarAdministradoresUseCase;
import com.uniquindio.nexuscultural.application.usecase.RegistrarAdministradorUseCase;
import com.uniquindio.nexuscultural.domain.entity.Administrador;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/administradores")
public class AdministradorController {

    private final RegistrarAdministradorUseCase registrarAdministradorUseCase;
    private final ConsultarAdministradorUseCase consultarAdministradorUseCase;
    private final ListarAdministradoresUseCase listarAdministradoresUseCase;
    private final ActualizarAdministradorUseCase actualizarAdministradorUseCase;
    private final CambiarNivelAccesoAdministradorUseCase cambiarNivelAccesoAdministradorUseCase;
    private final EliminarAdministradorUseCase eliminarAdministradorUseCase;

    public AdministradorController(RegistrarAdministradorUseCase registrarAdministradorUseCase,
                                   ConsultarAdministradorUseCase consultarAdministradorUseCase,
                                   ListarAdministradoresUseCase listarAdministradoresUseCase,
                                   ActualizarAdministradorUseCase actualizarAdministradorUseCase,
                                   CambiarNivelAccesoAdministradorUseCase cambiarNivelAccesoAdministradorUseCase,
                                   EliminarAdministradorUseCase eliminarAdministradorUseCase) {
        this.registrarAdministradorUseCase = registrarAdministradorUseCase;
        this.consultarAdministradorUseCase = consultarAdministradorUseCase;
        this.listarAdministradoresUseCase = listarAdministradoresUseCase;
        this.actualizarAdministradorUseCase = actualizarAdministradorUseCase;
        this.cambiarNivelAccesoAdministradorUseCase = cambiarNivelAccesoAdministradorUseCase;
        this.eliminarAdministradorUseCase = eliminarAdministradorUseCase;
    }

    // GET /api/administradores
    @GetMapping
    public ResponseEntity<List<AdministradorResponse>> listar() {
        List<Administrador> admins = listarAdministradoresUseCase.ejecutar();
        List<AdministradorResponse> response = admins.stream()
                .map(AdministradorResponse::desdeDominio)
                .toList();
        return ResponseEntity.ok(response);
    }

    // POST /api/administradores
    @PostMapping
    public ResponseEntity<AdministradorResponse> registrar(@Valid @RequestBody CrearAdministradorRequest request) {
        Administrador admin = registrarAdministradorUseCase.ejecutar(
                request.nombreCompleto(),
                request.email(),
                request.nivelAcceso()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(AdministradorResponse.desdeDominio(admin));
    }

    // GET /api/administradores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AdministradorResponse> obtenerPorId(@PathVariable UUID id) {
        Administrador admin = consultarAdministradorUseCase.ejecutar(id);
        return ResponseEntity.ok(AdministradorResponse.desdeDominio(admin));
    }

    // PUT /api/administradores/{id}
    @PutMapping("/{id}")
    public ResponseEntity<AdministradorResponse> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody ActualizarAdministradorRequest request) {
        Administrador admin = actualizarAdministradorUseCase.ejecutar(
                id,
                request.nombreCompleto(),
                request.nivelAcceso()
        );
        return ResponseEntity.ok(AdministradorResponse.desdeDominio(admin));
    }

    // PUT /api/administradores/{id}/nivel_acceso
    @PutMapping("/{id}/nivel_acceso")
    public ResponseEntity<AdministradorResponse> cambiarNivelAcceso(
            @PathVariable UUID id,
            @Valid @RequestBody CambiarNivelAccesoRequest request) {
        Administrador admin = cambiarNivelAccesoAdministradorUseCase.ejecutar(id, request.nivelAcceso());
        return ResponseEntity.ok(AdministradorResponse.desdeDominio(admin));
    }

    // DELETE /api/administradores/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        eliminarAdministradorUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }
}