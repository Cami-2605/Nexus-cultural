package com.uniquindio.nexuscultural.infraestructure.rest;

import com.uniquindio.nexuscultural.application.dto.request.ConfirmarPagoWompiRequest;
import com.uniquindio.nexuscultural.application.dto.response.PedidoResponse;
import com.uniquindio.nexuscultural.application.dto.response.WompiCheckoutSessionResponse;
import com.uniquindio.nexuscultural.application.service.WompiService;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pagos/wompi")
public class WompiPagoController {

    private static final Logger log = LoggerFactory.getLogger(WompiPagoController.class);

    private final WompiService wompiService;

    public WompiPagoController(WompiService wompiService) {
        this.wompiService = wompiService;
    }

    /**
     * POST /api/pagos/wompi/checkout-session
     * Genera la sesión de pago con firma de integridad SHA-256 para el Checkout Web de Wompi.
     */
    @PostMapping("/checkout-session")
    public ResponseEntity<WompiCheckoutSessionResponse> crearSesionCheckout(
            @RequestParam UUID pedidoId,
            @RequestParam(required = false) String redirectUrl) {
        WompiCheckoutSessionResponse response = wompiService.crearSesionCheckout(pedidoId, redirectUrl);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/pagos/wompi/confirmar
     * Confirma el pago de un pedido tras completarse en el Widget de Wompi o mediante simulación.
     */
    @PostMapping("/confirmar")
    public ResponseEntity<PedidoResponse> confirmarPago(@Valid @RequestBody ConfirmarPagoWompiRequest request) {
        Pedido pedido = wompiService.confirmarPago(request);
        return ResponseEntity.ok(toResponse(pedido));
    }

    /**
     * POST /api/pagos/wompi/webhook
     * Endpoint para recibir notificaciones automáticas asíncronas de Wompi.
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, String>> procesarWebhook(@RequestBody(required = false) Map<String, Object> payload) {
        log.info("Webhook recibido de Wompi: {}", payload);
        // Si viene información de transacción en el webhook, procesamos
        try {
            if (payload != null && payload.containsKey("data")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) payload.get("data");
                if (data != null && data.containsKey("transaction")) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> transaction = (Map<String, Object>) data.get("transaction");
                    String status = (String) transaction.get("status");
                    String reference = (String) transaction.get("reference");
                    String id = (String) transaction.get("id");
                    log.info("Transacción Wompi reference={}, status={}, id={}", reference, status, id);
                }
            }
        } catch (Exception e) {
            log.warn("Error parseando webhook Wompi: {}", e.getMessage());
        }
        return ResponseEntity.ok(Map.of("status", "received"));
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
