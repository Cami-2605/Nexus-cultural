package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.DevolverProductoRequest;
import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.application.service.PedidoService;
import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    /** POST /api/pedidos — El comprador crea un pedido para un artículo */
    @PostMapping
    public ResponseEntity<Pedido> crear(
            @RequestParam UUID compradorId,
            @RequestParam UUID articuloId,
            @RequestParam int cantidad) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crearPedido(compradorId, articuloId, cantidad));
    }

    /** POST /api/pedidos/pago — Registra el pago de un pedido pendiente */
    @PostMapping("/pago")
    public ResponseEntity<Pedido> procesarPago(@Valid @RequestBody ProcesarPagoRequest request) {
        return ResponseEntity.ok(pedidoService.procesarPago(request));
    }

    /** PATCH /api/pedidos/{id}/cancelar — El comprador cancela un pedido pendiente */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Pedido> cancelar(
            @PathVariable UUID id,
            @RequestParam UUID solicitanteId) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(id, solicitanteId));
    }

    /** POST /api/pedidos/devolucion — El comprador solicita devolver un pedido entregado */
    @PostMapping("/devolucion")
    public ResponseEntity<Devolucion> solicitarDevolucion(
            @Valid @RequestBody DevolverProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.solicitarDevolucion(request));
    }

    /** PATCH /api/pedidos/{id}/devolucion/resolver — El administrador aprueba o rechaza la devolución */
    @PatchMapping("/{id}/devolucion/resolver")
    public ResponseEntity<Pedido> resolverDevolucion(
            @PathVariable UUID id,
            @RequestParam boolean aprobar) {
        return ResponseEntity.ok(pedidoService.resolverDevolucion(id, aprobar));
    }
}
