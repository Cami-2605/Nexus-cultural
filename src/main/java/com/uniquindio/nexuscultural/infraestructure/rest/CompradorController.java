package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.*;
import com.uniquindio.nexuscultural.application.dto.response.CompradorResponse;
import com.uniquindio.nexuscultural.application.usecase.*;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.CompradorMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/compradores")
public class CompradorController {

    private final RegistrarCompradorUseCase registrarCompradorUseCase;
    private final ConsultarPerfilCompradorUseCase consultarPerfilCompradorUseCase;
    private final ActualizarPerfilCompradorUseCase actualizarPerfilCompradorUseCase;
    private final ActualizarDireccionEntregaUseCase actualizarDireccionEntregaUseCase;
    private final GestionarFavoritosUseCase gestionarFavoritosUseCase;
    private final CompradorMapper compradorMapper; // Inyección del Mapper
    private final com.uniquindio.nexuscultural.infraestructure.security.SecurityAuthorizationService securityAuthorizationService;

    public CompradorController(RegistrarCompradorUseCase registrarCompradorUseCase,
                               ConsultarPerfilCompradorUseCase consultarPerfilCompradorUseCase,
                               ActualizarPerfilCompradorUseCase actualizarPerfilCompradorUseCase,
                               ActualizarDireccionEntregaUseCase actualizarDireccionEntregaUseCase,
                               GestionarFavoritosUseCase gestionarFavoritosUseCase,
                               CompradorMapper compradorMapper,
                               com.uniquindio.nexuscultural.infraestructure.security.SecurityAuthorizationService securityAuthorizationService) {
        this.registrarCompradorUseCase = registrarCompradorUseCase;
        this.consultarPerfilCompradorUseCase = consultarPerfilCompradorUseCase;
        this.actualizarPerfilCompradorUseCase = actualizarPerfilCompradorUseCase;
        this.actualizarDireccionEntregaUseCase = actualizarDireccionEntregaUseCase;
        this.gestionarFavoritosUseCase = gestionarFavoritosUseCase;
        this.compradorMapper = compradorMapper;
        this.securityAuthorizationService = securityAuthorizationService;
    }

    @PostMapping
    public ResponseEntity<CompradorResponse> registrarComprador(@Valid @RequestBody RegistrarCompradorRequest request) {
        UbicacionGeografica direccion = new UbicacionGeografica(
                request.departamento(),
                request.municipio(),
                request.direccion()
        );

        Comprador comprador = registrarCompradorUseCase.ejecutar(
                request.nombreCompleto(),
                request.email(),
                direccion
        );

        //return ResponseEntity.ok(compradorMapper.toResponse(comprador)); //devuelve 200
        return ResponseEntity.status(HttpStatus.CREATED).body(compradorMapper.toResponse(comprador));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompradorResponse> consultarPerfil(@PathVariable UUID id) {
        securityAuthorizationService.validarAccesoRecursoOAdmin(id);
        Comprador comprador = consultarPerfilCompradorUseCase.ejecutar(id);
        return ResponseEntity.ok(compradorMapper.toResponse(comprador));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompradorResponse> actualizarPerfil(@PathVariable UUID id,
                                                              @Valid @RequestBody ActualizarPerfilCompradorRequest request) {
        securityAuthorizationService.validarAccesoRecursoOAdmin(id);
        Comprador comprador = actualizarPerfilCompradorUseCase.ejecutar(id, request.nombreCompleto());
        return ResponseEntity.ok(compradorMapper.toResponse(comprador));
    }

    @PutMapping("/{id}/direccion")
    public ResponseEntity<CompradorResponse> actualizarDireccion(@PathVariable UUID id,
                                                                 @Valid @RequestBody ActualizarDireccionRequest request) {
        securityAuthorizationService.validarAccesoRecursoOAdmin(id);
        UbicacionGeografica nuevaDireccion = new UbicacionGeografica(
                request.departamento(),
                request.municipio(),
                request.direccion()
        );

        Comprador comprador = actualizarDireccionEntregaUseCase.ejecutar(id, nuevaDireccion);
        return ResponseEntity.ok(compradorMapper.toResponse(comprador));
    }

    @PostMapping("/{id}/favoritos")
    public ResponseEntity<CompradorResponse> agregarFavorito(@PathVariable UUID id,
                                                             @Valid @RequestBody AgregarFavoritoRequest request) {
        securityAuthorizationService.validarAccesoRecursoOAdmin(id);
        Comprador comprador = gestionarFavoritosUseCase.agregarFavorito(id, request.articuloId());
        return ResponseEntity.ok(compradorMapper.toResponse(comprador));
    }

    @GetMapping("/{id}/favoritos")
    public ResponseEntity<List<UUID>> consultarFavoritos(@PathVariable UUID id) {
        securityAuthorizationService.validarAccesoRecursoOAdmin(id);
        List<UUID> favoritos = gestionarFavoritosUseCase.consultarFavoritos(id);
        return ResponseEntity.ok(favoritos);
    }

    @DeleteMapping("/{id}/favoritos/{articuloId}")
    public ResponseEntity<CompradorResponse> eliminarFavorito(@PathVariable UUID id,
                                                              @PathVariable UUID articuloId) {
        securityAuthorizationService.validarAccesoRecursoOAdmin(id);
        Comprador comprador = gestionarFavoritosUseCase.eliminarFavorito(id, articuloId);
        return ResponseEntity.ok(compradorMapper.toResponse(comprador));
    }
}