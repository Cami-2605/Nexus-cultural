package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.ActualizarCantidadItemRequest;
import com.uniquindio.nexuscultural.application.dto.request.AgregarItemCarritoRequest;
import com.uniquindio.nexuscultural.application.dto.response.CarritoItemResponse;
import com.uniquindio.nexuscultural.application.service.CarritoService;
import com.uniquindio.nexuscultural.domain.entity.CarritoItem;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para la gestión del carrito de compras.
 * Todos los endpoints son sub-recursos de /api/compradores/{compradorId}/carrito.
 */
@RestController
@RequestMapping("/api/compradores/{compradorId}/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    /** POST /api/compradores/{compradorId}/carrito/items — Agrega un artículo al carrito. */
    @PostMapping("/items")
    public ResponseEntity<CarritoItemResponse> agregarItem(
            @PathVariable UUID compradorId,
            @Valid @RequestBody AgregarItemCarritoRequest request) {
        CarritoItem item = carritoService.agregarItem(compradorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(item));
    }

    /** GET /api/compradores/{compradorId}/carrito — Retorna el carrito completo del comprador. */
    @GetMapping
    public ResponseEntity<List<CarritoItemResponse>> obtenerCarrito(@PathVariable UUID compradorId) {
        List<CarritoItemResponse> respuesta = carritoService.obtenerCarrito(compradorId)
                .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(respuesta);
    }

    /** PUT /api/compradores/{compradorId}/carrito/items/{itemId} — Actualiza la cantidad de un ítem. */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CarritoItemResponse> actualizarCantidad(
            @PathVariable UUID compradorId,
            @PathVariable UUID itemId,
            @Valid @RequestBody ActualizarCantidadItemRequest request) {
        CarritoItem item = carritoService.actualizarCantidad(compradorId, itemId, request.cantidad());
        return ResponseEntity.ok(toResponse(item));
    }

    /** DELETE /api/compradores/{compradorId}/carrito/items/{itemId} — Elimina un ítem del carrito. */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> eliminarItem(
            @PathVariable UUID compradorId,
            @PathVariable UUID itemId) {
        carritoService.eliminarItem(compradorId, itemId);
        return ResponseEntity.noContent().build();
    }

    /** DELETE /api/compradores/{compradorId}/carrito — Vacía todo el carrito del comprador. */
    @DeleteMapping
    public ResponseEntity<Void> vaciarCarrito(@PathVariable UUID compradorId) {
        carritoService.vaciarCarrito(compradorId);
        return ResponseEntity.noContent().build();
    }

    private CarritoItemResponse toResponse(CarritoItem item) {
        return new CarritoItemResponse(
                item.getId(),
                item.getCompradorId(),
                item.getArticuloId(),
                item.getCantidad()
        );
    }
}
