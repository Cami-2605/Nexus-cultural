package com.uniquindio.nexuscultural.infraestructure.web;

import com.uniquindio.nexuscultural.application.dto.request.*;
import com.uniquindio.nexuscultural.application.dto.response.CompradorResponse;
import com.uniquindio.nexuscultural.application.usecase.*;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/compradores")
public class CompradorController {

    private final RegistrarCompradorUseCase registrarCompradorUseCase;
    private final ConsultarPerfilCompradorUseCase consultarPerfilCompradorUseCase;
    private final ActualizarPerfilCompradorUseCase actualizarPerfilCompradorUseCase;
    private final ActualizarDireccionEntregaUseCase actualizarDireccionEntregaUseCase;
    private final GestionarFavoritosUseCase gestionarFavoritosUseCase;

    public CompradorController(RegistrarCompradorUseCase registrarCompradorUseCase,
                               ConsultarPerfilCompradorUseCase consultarPerfilCompradorUseCase,
                               ActualizarPerfilCompradorUseCase actualizarPerfilCompradorUseCase,
                               ActualizarDireccionEntregaUseCase actualizarDireccionEntregaUseCase,
                               GestionarFavoritosUseCase gestionarFavoritosUseCase) {
        this.registrarCompradorUseCase = registrarCompradorUseCase;
        this.consultarPerfilCompradorUseCase = consultarPerfilCompradorUseCase;
        this.actualizarPerfilCompradorUseCase = actualizarPerfilCompradorUseCase;
        this.actualizarDireccionEntregaUseCase = actualizarDireccionEntregaUseCase;
        this.gestionarFavoritosUseCase = gestionarFavoritosUseCase;
    }

    @PostMapping
    public ResponseEntity<CompradorResponse> registrarComprador(@RequestBody RegistrarCompradorRequest request) {
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

        return ResponseEntity.ok(mapearAResponse(comprador));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompradorResponse> consultarPerfil(@PathVariable UUID id) {
        Comprador comprador = consultarPerfilCompradorUseCase.ejecutar(id);
        return ResponseEntity.ok(mapearAResponse(comprador));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompradorResponse> actualizarPerfil(@PathVariable UUID id,
                                                              @RequestBody ActualizarPerfilCompradorRequest request) {
        Comprador comprador = actualizarPerfilCompradorUseCase.ejecutar(id, request.nombreCompleto());
        return ResponseEntity.ok(mapearAResponse(comprador));
    }

    @PutMapping("/{id}/direccion")
    public ResponseEntity<CompradorResponse> actualizarDireccion(@PathVariable UUID id,
                                                                 @RequestBody ActualizarDireccionRequest request) {
        UbicacionGeografica nuevaDireccion = new UbicacionGeografica(
                request.departamento(),
                request.municipio(),
                request.direccion()
        );

        Comprador comprador = actualizarDireccionEntregaUseCase.ejecutar(id, nuevaDireccion);
        return ResponseEntity.ok(mapearAResponse(comprador));
    }

    @PostMapping("/{id}/favoritos")
    public ResponseEntity<CompradorResponse> agregarFavorito(@PathVariable UUID id,
                                                             @RequestBody AgregarFavoritoRequest request) {
        Comprador comprador = gestionarFavoritosUseCase.agregarFavorito(id, request.articuloId());
        return ResponseEntity.ok(mapearAResponse(comprador));
    }

    @GetMapping("/{id}/favoritos")
    public ResponseEntity<List<UUID>> consultarFavoritos(@PathVariable UUID id) {
        List<UUID> favoritos = gestionarFavoritosUseCase.consultarFavoritos(id);
        return ResponseEntity.ok(favoritos);
    }

    @DeleteMapping("/{id}/favoritos/{articuloId}")
    public ResponseEntity<CompradorResponse> eliminarFavorito(@PathVariable UUID id,
                                                              @PathVariable UUID articuloId) {
        Comprador comprador = gestionarFavoritosUseCase.eliminarFavorito(id, articuloId);
        return ResponseEntity.ok(mapearAResponse(comprador));
    }

    private CompradorResponse mapearAResponse(Comprador comprador) {
        return new CompradorResponse(
                comprador.getId(),
                comprador.getNombreCompleto(),
                comprador.getEmail().valor(),
                comprador.getFechaRegistro(),
                comprador.getDireccionEntrega(),
                comprador.getFavoritos()
        );
    }
}