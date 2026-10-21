package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.application.service.CatalogoService;
import com.uniquindio.nexuscultural.domain.valueobject.Grupo;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArticuloMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Controller REST para exploración y búsqueda pública del catálogo artesanal.
 */
@RestController
@RequestMapping("/api")
public class CatalogoController {

    private final CatalogoService catalogoService;
    private final ArticuloMapper articuloMapper;

    public CatalogoController(CatalogoService catalogoService, ArticuloMapper articuloMapper) {
        this.catalogoService = catalogoService;
        this.articuloMapper = articuloMapper;
    }

    /**
     * GET /api/catalogo — Lista todos los artículos disponibles.
     * Acepta el parámetro opcional {@code grupo} para filtrar por categoría.
     */
    @GetMapping("/catalogo")
    public ResponseEntity<List<ArticuloDetalleResponse>> listarCatalogo(
            @RequestParam(required = false) Grupo grupo) {
        List<ArticuloDetalleResponse> respuesta = grupo != null
                ? catalogoService.buscarPorGrupo(grupo).stream().map(articuloMapper::toResponse).toList()
                : catalogoService.listarCatalogo().stream().map(articuloMapper::toResponse).toList();
        return ResponseEntity.ok(respuesta);
    }

    /** GET /api/catalogo/buscar?query= — Búsqueda de artículos por título. */
    @GetMapping("/catalogo/buscar")
    public ResponseEntity<List<ArticuloDetalleResponse>> buscar(
            @RequestParam String query) {
        List<ArticuloDetalleResponse> respuesta = catalogoService.buscarPorTitulo(query)
                .stream().map(articuloMapper::toResponse).toList();
        return ResponseEntity.ok(respuesta);
    }

    /** GET /api/catalogo/categorias — Lista todos los valores del enum Grupo. */
    @GetMapping("/catalogo/categorias")
    public ResponseEntity<List<String>> listarCategorias() {
        List<String> categorias = Arrays.stream(Grupo.values())
                .map(Grupo::name)
                .toList();
        return ResponseEntity.ok(categorias);
    }

    /** GET /api/catalogo/artesano/{artesanoId} — Artículos de un artesano específico. */
    @GetMapping("/catalogo/artesano/{artesanoId}")
    public ResponseEntity<List<ArticuloDetalleResponse>> listarPorArtesano(
            @PathVariable UUID artesanoId) {
        List<ArticuloDetalleResponse> respuesta = catalogoService.listarPorArtesano(artesanoId)
                .stream().map(articuloMapper::toResponse).toList();
        return ResponseEntity.ok(respuesta);
    }

    /** GET /api/articulos/{id} — Detalle de un artículo por ID. */
    @GetMapping("/articulos/{id}")
    public ResponseEntity<ArticuloDetalleResponse> obtenerArticulo(@PathVariable UUID id) {
        return ResponseEntity.ok(articuloMapper.toResponse(catalogoService.obtenerArticulo(id)));
    }
}
