package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarInventarioRequest;
import com.uniquindio.nexuscultural.application.dto.request.AplicarDescuentoRequest;
import com.uniquindio.nexuscultural.application.dto.request.CrearArticuloRequest;
import com.uniquindio.nexuscultural.application.dto.request.ReponerStockRequest;
import com.uniquindio.nexuscultural.application.dto.response.ArticuloDetalleResponse;
import com.uniquindio.nexuscultural.application.service.ArticuloService;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.infraestructure.rest.mapper.ArticuloMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/articulos")
        public class ArticuloController {

            private final ArticuloService articuloService;
            private final ArticuloMapper articuloMapper;

            public ArticuloController(ArticuloService articuloService, ArticuloMapper articuloMapper) {
                this.articuloService = articuloService;
                this.articuloMapper = articuloMapper;
            }

            /** POST /api/articulos — El artesano publica un nuevo artículo */
            @PostMapping
            public ResponseEntity<ArticuloDetalleResponse> publicar(@Valid @RequestBody CrearArticuloRequest request) {
                Articulo articulo = articuloService.publicarArticulo(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(articuloMapper.toResponse(articulo));
            }

            /** PUT /api/articulos/{id} — El artesano actualiza los datos generales de su obra */
            @PutMapping("/{id}")
            public ResponseEntity<ArticuloDetalleResponse> actualizar(
                    @PathVariable UUID id,
                    @Valid @RequestBody CrearArticuloRequest request) {

                Articulo articulo = articuloService.actualizarArticulo(id, request);
                return ResponseEntity.ok(articuloMapper.toResponse(articulo));
            }

            /** PUT /api/articulos/{id}/inventario — Ajuste exacto de inventario */
            @PutMapping("/{id}/inventario")
            public ResponseEntity<ArticuloDetalleResponse> actualizarInventario(
                    @PathVariable UUID id,
                    @Valid @RequestBody ActualizarInventarioRequest request) {

                ActualizarInventarioRequest requestEfectivo = request;
                if (request.idArticulo() == null || !request.idArticulo().equals(id)) {
                    requestEfectivo = new ActualizarInventarioRequest(id, request.idArtesano(), request.cantidad(), request.motivo());
                }
                Articulo articulo = articuloService.actualizarInventario(requestEfectivo);
                return ResponseEntity.ok(articuloMapper.toResponse(articulo));
            }

            /** PATCH /api/articulos/{id}/reponer — Aumenta el stock en una cantidad delta */
            @PatchMapping("/{id}/reponer")
            public ResponseEntity<ArticuloDetalleResponse> reponerStock(
                    @PathVariable UUID id,
                    @Valid @RequestBody ReponerStockRequest request) {

                Articulo articulo = articuloService.reponerStock(id, request);
                return ResponseEntity.ok(articuloMapper.toResponse(articulo));
            }

            /** PATCH /api/articulos/{id}/inactivar — Desactiva el artículo (artesano o moderación) */
            @PatchMapping("/{id}/inactivar")
            public ResponseEntity<ArticuloDetalleResponse> inactivar(
                    @PathVariable UUID id,
                    @RequestParam(required = false) UUID artesanoId) {

                Articulo articulo = articuloService.inactivarArticulo(id, artesanoId);
                return ResponseEntity.ok(articuloMapper.toResponse(articulo));
            }

            /** PATCH /api/articulos/{id}/reactivar — Vuelve a poner el artículo en venta */
            @PatchMapping("/{id}/reactivar")
            public ResponseEntity<ArticuloDetalleResponse> reactivar(
                    @PathVariable UUID id,
                    @RequestParam(required = false) UUID artesanoId) {

                Articulo articulo = articuloService.reactivarArticulo(id, artesanoId);
                return ResponseEntity.ok(articuloMapper.toResponse(articulo));
            }

            /** POST /api/articulos/{id}/descuento — Aplica un descuento al artículo */
            @PostMapping("/{id}/descuento")
            public ResponseEntity<ArticuloDetalleResponse> aplicarDescuento(
                    @PathVariable UUID id,
                    @Valid @RequestBody AplicarDescuentoRequest request) {
                Articulo articulo = articuloService.aplicarDescuento(id, request);
                return ResponseEntity.ok(articuloMapper.toResponse(articulo));
            }

            /** DELETE /api/articulos/{id}/descuento — Remueve el descuento del artículo */
            @DeleteMapping("/{id}/descuento")
            public ResponseEntity<ArticuloDetalleResponse> removerDescuento(
                    @PathVariable UUID id,
                    @RequestParam UUID artesanoId) {
                Articulo articulo = articuloService.removerDescuento(id, artesanoId);
                return ResponseEntity.ok(articuloMapper.toResponse(articulo));
            }

            /** DELETE /api/articulos/{id} — El artesano elimina su artículo del catálogo */
            @DeleteMapping("/{id}")
            public ResponseEntity<Void> eliminar(
                    @PathVariable UUID id,
                    @RequestParam(required = false) UUID artesanoId) {
                articuloService.eliminarArticulo(id, artesanoId);
                return ResponseEntity.noContent().build();
            }
        }