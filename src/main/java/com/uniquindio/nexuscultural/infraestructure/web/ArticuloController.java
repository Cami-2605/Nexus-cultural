package com.uniquindio.nexuscultural.infraestructure.web;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarInventarioRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearArticuloRequest;
import com.uniquindio.nexuscultural.application.dto.request.ReponerStockRequest;
import com.uniquindio.nexuscultural.application.service.ArticuloService;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/articulos")
public class ArticuloController {

    private final ArticuloService articuloService;

    public ArticuloController(ArticuloService articuloService) {
        this.articuloService = articuloService;
    }

    /** POST /api/articulos — El artesano publica un nuevo artículo */
    @PostMapping
    public ResponseEntity<Articulo> publicar(@Valid @RequestBody CrearArticuloRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articuloService.publicarArticulo(request));
    }

    /** PUT /api/articulos/inventario — Ajuste exacto de inventario (conteo físico / corrección) */
    @PutMapping("/inventario")
    public ResponseEntity<Articulo> actualizarInventario(
            @Valid @RequestBody ActualizarInventarioRequest request) {
        return ResponseEntity.ok(articuloService.actualizarInventario(request));
    }

    /** PATCH /api/articulos/{id}/reponer — Aumenta el stock en una cantidad delta */
    @PatchMapping("/{id}/reponer")
    public ResponseEntity<Articulo> reponerStock(
            @PathVariable UUID id,
            @Valid @RequestBody ReponerStockRequest request) {
        return ResponseEntity.ok(articuloService.reponerStock(id, request));
    }

    /** PATCH /api/articulos/{id}/inactivar — El artesano saca temporalmente el artículo de venta */
    @PatchMapping("/{id}/inactivar")
    public ResponseEntity<Articulo> inactivar(
            @PathVariable UUID id,
            @RequestParam UUID artesanoId) {
        return ResponseEntity.ok(articuloService.inactivarArticulo(id, artesanoId));
    }

    /** PATCH /api/articulos/{id}/reactivar — El artesano vuelve a poner el artículo en venta */
    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Articulo> reactivar(
            @PathVariable UUID id,
            @RequestParam UUID artesanoId) {
        return ResponseEntity.ok(articuloService.reactivarArticulo(id, artesanoId));
    }
}
