package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.CrearPedidoRequest;
import com.uniquindio.nexuscultural.application.dto.request.DevolverProductoRequest;
import com.uniquindio.nexuscultural.application.dto.request.ProcesarPagoRequest;
import com.uniquindio.nexuscultural.application.dto.response.PedidoResponse;
import com.uniquindio.nexuscultural.application.service.PedidoService;
import com.uniquindio.nexuscultural.domain.entity.Devolucion;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final com.uniquindio.nexuscultural.infraestructure.security.SecurityAuthorizationService securityAuthorizationService;

    public PedidoController(PedidoService pedidoService,
                            com.uniquindio.nexuscultural.infraestructure.security.SecurityAuthorizationService securityAuthorizationService) {
        this.pedidoService = pedidoService;
        this.securityAuthorizationService = securityAuthorizationService;
    }

    /** POST /api/pedidos — El comprador crea un pedido para un artículo */
    @PostMapping
    public ResponseEntity<PedidoResponse> crear(
            @RequestParam(required = false) UUID compradorId,
            @RequestParam(required = false) UUID articuloId,
            @RequestParam(required = false) Integer cantidad,
            @Valid @RequestBody(required = false) CrearPedidoRequest request) {

        UUID finalCompradorId = request != null ? request.compradorId() : compradorId;
        securityAuthorizationService.validarAccesoRecursoOAdmin(finalCompradorId);
        UUID finalArticuloId = request != null ? request.articuloId() : articuloId;
        int finalCantidad = request != null ? request.cantidad() : (cantidad != null ? cantidad : 1);

        Pedido pedido = pedidoService.crearPedido(finalCompradorId, finalArticuloId, finalCantidad);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(pedido));
    }

    /** GET /api/pedidos/{id} — Consulta detalle de un pedido por ID */
    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable UUID id) {
        Pedido pedido = pedidoService.obtenerPorId(id);
        securityAuthorizationService.validarAccesoRecursoOAdmin(pedido.getCompradorId());
        return ResponseEntity.ok(toResponse(pedido));
    }

    /** GET /api/pedidos/comprador/{compradorId} — Lista pedidos realizados por un comprador */
    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<List<PedidoResponse>> listarPorComprador(@PathVariable UUID compradorId) {
        securityAuthorizationService.validarAccesoRecursoOAdmin(compradorId);
        List<PedidoResponse> pedidos = pedidoService.listarPorComprador(compradorId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    /** GET /api/pedidos/artesano/{artesanoId} — Lista pedidos de obras vendidas por un artesano */
    @GetMapping("/artesano/{artesanoId}")
    public ResponseEntity<List<PedidoResponse>> listarPorArtesano(@PathVariable UUID artesanoId) {
        securityAuthorizationService.validarAccesoRecursoOAdmin(artesanoId);
        List<PedidoResponse> pedidos = pedidoService.listarPorArtesano(artesanoId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    /** POST /api/pedidos/pago — Registra el pago de un pedido pendiente */
    @PostMapping("/pago")
    public ResponseEntity<PedidoResponse> procesarPago(@Valid @RequestBody ProcesarPagoRequest request) {
        return ResponseEntity.ok(toResponse(pedidoService.procesarPago(request)));
    }

    /** PATCH /api/pedidos/{id}/avanzar — Avanza el estado del pedido (PENDIENTE -> CONFIRMADO -> EN_CAMINO -> ENTREGADO) */
    @PatchMapping("/{id}/avanzar")
    public ResponseEntity<PedidoResponse> avanzarEstado(@PathVariable UUID id) {
        return ResponseEntity.ok(toResponse(pedidoService.avanzarEstado(id)));
    }

    /** PATCH /api/pedidos/{id}/entregar — Marca directamente el pedido como ENTREGADO para pruebas de garantía */
    @PatchMapping("/{id}/entregar")
    public ResponseEntity<PedidoResponse> marcarEntregado(@PathVariable UUID id) {
        Pedido pedido = pedidoService.obtenerPorId(id);
        int maxIteraciones = 5;
        while (pedido.getEstado() != com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido.ENTREGADO
                && pedido.getEstado() != com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido.CANCELADO
                && pedido.getEstado() != com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido.DEVUELTO
                && maxIteraciones-- > 0) {
            pedido = pedidoService.avanzarEstado(id);
        }
        return ResponseEntity.ok(toResponse(pedido));
    }

    /** PATCH /api/pedidos/{id}/cancelar — El comprador cancela un pedido pendiente */
    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<PedidoResponse> cancelar(
            @PathVariable UUID id,
            @RequestParam UUID solicitanteId) {
        return ResponseEntity.ok(toResponse(pedidoService.cancelarPedido(id, solicitanteId)));
    }

    /** POST /api/pedidos/devolucion — El comprador solicita devolver un pedido entregado */
    @PostMapping("/devolucion")
    public ResponseEntity<Devolucion> solicitarDevolucion(
            @Valid @RequestBody DevolverProductoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.solicitarDevolucion(request));
    }

    /** GET /api/pedidos/devoluciones — Lista solicitudes de devolución para el panel de administración */
    @GetMapping("/devoluciones")
    public ResponseEntity<List<com.uniquindio.nexuscultural.application.dto.response.DevolucionDetalleResponse>> listarDevoluciones() {
        return ResponseEntity.ok(pedidoService.listarDevoluciones());
    }

    /** PATCH /api/pedidos/{id}/devolucion/resolver — El administrador aprueba o rechaza la devolución */
    @PatchMapping("/{id}/devolucion/resolver")
    public ResponseEntity<PedidoResponse> resolverDevolucion(
            @PathVariable UUID id,
            @RequestParam boolean aprobar) {
        return ResponseEntity.ok(toResponse(pedidoService.resolverDevolucion(id, aprobar)));
    }

    private PedidoResponse toResponse(Pedido pedido) {
        if (pedido == null) return null;
        String motivoDev = pedido.getDevolucion() != null ? pedido.getDevolucion().getMotivo() : null;
        String estadoDev = pedido.getDevolucion() != null && pedido.getDevolucion().getEstado() != null
                ? pedido.getDevolucion().getEstado().name() : null;
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCompradorId(),
                pedido.getArticuloId(),
                pedido.getCantidad(),
                pedido.getTotal() != null ? pedido.getTotal().monto() : BigDecimal.ZERO,
                pedido.getTotal() != null ? pedido.getTotal().moneda() : "COP",
                pedido.getEstado() != null ? pedido.getEstado().name() : "PENDIENTE",
                pedido.getFechaCreacion(),
                pedido.getFechaEntrega(),
                pedido.getMetodoPago(),
                pedido.getReferenciaTransaccion(),
                motivoDev,
                estadoDev
        );
    }
}
