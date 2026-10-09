package com.uniquindio.nexuscultural.infraestructure.web;

import com.uniquindio.nexuscultural.application.dto.request.RegistrarArtesanoRequest;
import com.uniquindio.nexuscultural.application.dto.response.ArtesanoResponse;
import com.uniquindio.nexuscultural.application.usecase.RegistrarArtesanoUseCase;
import com.uniquindio.nexuscultural.domain.entity.Artesano;
import com.uniquindio.nexuscultural.domain.valueobject.TrayectoriaCultural;
import com.uniquindio.nexuscultural.domain.valueobject.UbicacionGeografica;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/artesanos")
public class ArtesanoController {

    private final RegistrarArtesanoUseCase registrarArtesanoUseCase;

    public ArtesanoController(RegistrarArtesanoUseCase registrarArtesanoUseCase) {
        this.registrarArtesanoUseCase = registrarArtesanoUseCase;
    }

    @PostMapping
    public ResponseEntity<ArtesanoResponse> registrarArtesano(@RequestBody RegistrarArtesanoRequest request) {

        // 1. El adaptador traduce los datos primitivos del JSON DTO a Value Objects
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

        // 3. El adaptador mapea la Entidad de Dominio a la Respuesta JSON (DTO)
        ArtesanoResponse respuesta = new ArtesanoResponse(
                artesanoCreado.getId(),
                artesanoCreado.getNombreCompleto(),
                artesanoCreado.getEmail().valor(),
                artesanoCreado.getFechaRegistro(),
                artesanoCreado.isActivo(),
                artesanoCreado.getTrayectoriaCultural(),
                artesanoCreado.getUbicacionGeografica()
        );

        return ResponseEntity.ok(respuesta);
    }
}
