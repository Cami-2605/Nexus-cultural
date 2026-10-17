package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.RegistrarArtesanoRequest;
import com.uniquindio.nexuscultural.application.dto.response.ArtesanoResponse;
import com.uniquindio.nexuscultural.application.dto.response.LogroResponse;
import com.uniquindio.nexuscultural.application.usecase.RegistrarArtesanoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArtesanoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArtesanoMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Esta clase maneja peticiones HTTP y devuelve un JSON directamente
@RestController
@RequestMapping("/api/artesanos") // URL base de todos los métodos de esta clase
public class ArtesanoController {

    private final RegistrarArtesanoUseCase registrarArtesanoUseCase;
    private final ArtesanoMapper artesanoMapper;
    private final ArtesanoRepository artesanoRepository;

    public ArtesanoController(RegistrarArtesanoUseCase registrarArtesanoUseCase,
                              ArtesanoMapper artesanoMapper,
                              ArtesanoRepository artesanoRepository) {
        this.registrarArtesanoUseCase = registrarArtesanoUseCase;
        this.artesanoMapper = artesanoMapper;
        this.artesanoRepository = artesanoRepository;
    }

    @PostMapping
    public ResponseEntity<ArtesanoResponse> registrarArtesano(@Valid @RequestBody RegistrarArtesanoRequest request) {

        // 1. Traduce los datos primitivos del DTO a Value Objects
        TrayectoriaCultural trayectoria = new TrayectoriaCultural(
                request.anosExperiencia(),
                request.reconocimientos()
        );

        UbicacionGeografica ubicacion = new UbicacionGeografica(
                request.departamento(),
                request.municipio(),
                request.direccion()
        );

        // 2. Invoca la lógica pura de aplicación
        Artesano artesanoCreado = registrarArtesanoUseCase.ejecutar(
                request.nombreCompleto(),
                request.email(),
                trayectoria,
                ubicacion
        );

        // 3. Delegamos la transformación de la Entidad a DTO usando el Mapper
        // Devuelve un código 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(artesanoMapper.toResponse(artesanoCreado));
    }

    /** GET /api/artesanos/{id}/logros — Retorna los logros del artesano. */
    @GetMapping("/{id}/logros")
    public ResponseEntity<java.util.List<LogroResponse>> consultarLogros(@PathVariable java.util.UUID id) {
        Artesano artesano = artesanoRepository.buscarPorId(id)
                .orElseThrow(() -> new ReglaDominioException("Artesano no encontrado."));
        java.util.List<LogroResponse> logros = artesano.getLogros().stream()
                .map(l -> new LogroResponse(l.getNombre(), l.getDescripcion()))
                .toList();
        return ResponseEntity.ok(logros);
    }
}