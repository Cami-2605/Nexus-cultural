package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.RegistrarArtesanoRequest;
import com.uniquindio.nexuscultural.application.dto.response.ArtesanoResponse;
import com.uniquindio.nexuscultural.application.usecase.RegistrarArtesanoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArtesanoMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Esta clase maneja peticiones HTTP y devuelve un JSON directamente
@RestController
@RequestMapping("/artesanos") // URL base de todos los métodos de esta clase
public class ArtesanoController {

    private final RegistrarArtesanoUseCase registrarArtesanoUseCase;
    private final ArtesanoMapper artesanoMapper; // Inyección del Mapper

    public ArtesanoController(RegistrarArtesanoUseCase registrarArtesanoUseCase,
                              ArtesanoMapper artesanoMapper) {
        this.registrarArtesanoUseCase = registrarArtesanoUseCase;
        this.artesanoMapper = artesanoMapper;
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
        return ResponseEntity.ok(artesanoMapper.toResponse(artesanoCreado));
    }
}