package com.uniquindio.nexuscultural.application.service;

import com.uniquindio.nexuscultural.application.dto.request.ConfirmarPagoWompiRequest;
import com.uniquindio.nexuscultural.application.dto.response.DevolucionDetalleResponse;
import com.uniquindio.nexuscultural.application.dto.response.WompiCheckoutSessionResponse;
import com.uniquindio.nexuscultural.domain.entity.Articulo;
import com.uniquindio.nexuscultural.domain.entity.Comprador;
import com.uniquindio.nexuscultural.domain.entity.Pedido;
import com.uniquindio.nexuscultural.domain.exception.ReglaDominioException;
import com.uniquindio.nexuscultural.domain.repository.ArticuloRepository;
import com.uniquindio.nexuscultural.domain.repository.CompradorRepository;
import com.uniquindio.nexuscultural.domain.repository.PedidoRepository;
import com.uniquindio.nexuscultural.domain.valueobject.EstadoPedido;
import com.uniquindio.nexuscultural.domain.valueobject.Precio;
import com.uniquindio.nexuscultural.infraestructure.config.WompiConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class WompiService {

    private static final Logger log = LoggerFactory.getLogger(WompiService.class);

    private final WompiConfig wompiConfig;
    private final PedidoRepository pedidoRepository;
    private final ArticuloRepository articuloRepository;
    private final CompradorRepository compradorRepository;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public WompiService(WompiConfig wompiConfig,
                        PedidoRepository pedidoRepository,
                        ArticuloRepository articuloRepository,
                        CompradorRepository compradorRepository) {
        this.wompiConfig = wompiConfig;
        this.pedidoRepository = pedidoRepository;
        this.articuloRepository = articuloRepository;
        this.compradorRepository = compradorRepository;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Calcula la firma de integridad requerida por Wompi:
     * SHA-256(reference + amountInCents + currency + integritySecret) en hexadecimal minúsculas.
     */
    public String generarFirmaIntegridad(String reference, long amountInCents, String currency) {
        try {
            String secret = wompiConfig.getIntegritySecret();
            String rawData = reference + amountInCents + currency + secret;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawData.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar la firma de integridad Wompi", e);
        }
    }

    /**
     * Crea una transacción REAL en Wompi Sandbox Cloud para que quede registrada
     * en el panel de comercios de Wompi (https://comercios.wompi.co/).
     */
    public String crearTransaccionRealWompiCloud(Pedido pedido) {
        try {
            BigDecimal montoTotal = pedido.getTotal().monto();
            long amountInCents = montoTotal.multiply(BigDecimal.valueOf(100)).longValue();
            String currency = pedido.getTotal().moneda() != null ? pedido.getTotal().moneda() : "COP";
            String reference = "NEXUS-" + pedido.getId().toString().substring(0, 8) + "-" + System.currentTimeMillis();

            // 1. Obtener acceptance_token y accept_personal_auth de Wompi
            HttpRequest infoReq = HttpRequest.newBuilder()
                    .uri(URI.create(wompiConfig.getApiUrl() + "/merchants/info"))
                    .header("x-merchant-public-key", wompiConfig.getPublicKey())
                    .GET()
                    .timeout(Duration.ofSeconds(8))
                    .build();
            HttpResponse<String> infoResp = httpClient.send(infoReq, HttpResponse.BodyHandlers.ofString());
            JsonNode infoJson = objectMapper.readTree(infoResp.body());
            String acceptToken = infoJson.path("data").path("presigned_acceptance").path("acceptance_token").asText();
            String dataToken = infoJson.path("data").path("presigned_personal_data_auth").path("acceptance_token").asText();

            // 2. Tokenizar tarjeta de pruebas de Wompi (4242 4242...)
            Map<String, Object> cardPayload = Map.of(
                    "number", "4242424242424242",
                    "cvc", "123",
                    "exp_month", "12",
                    "exp_year", "30",
                    "card_holder", "Wilson Delgado"
            );
            HttpRequest tokenReq = HttpRequest.newBuilder()
                    .uri(URI.create(wompiConfig.getApiUrl() + "/tokens/cards"))
                    .header("Authorization", "Bearer " + wompiConfig.getPublicKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(cardPayload)))
                    .timeout(Duration.ofSeconds(8))
                    .build();
            HttpResponse<String> tokenResp = httpClient.send(tokenReq, HttpResponse.BodyHandlers.ofString());
            JsonNode tokenJson = objectMapper.readTree(tokenResp.body());
            String cardToken = tokenJson.path("data").path("id").asText();

            // 3. Firma de integridad SHA-256
            String signature = generarFirmaIntegridad(reference, amountInCents, currency);

            // 4. Crear la transacción en Wompi Cloud
            Map<String, Object> txPayload = Map.of(
                    "acceptance_token", acceptToken,
                    "accept_personal_auth", dataToken,
                    "amount_in_cents", amountInCents,
                    "currency", currency,
                    "signature", signature,
                    "customer_email", "wilvive1@gmail.com",
                    "payment_method", Map.of(
                            "type", "CARD",
                            "token", cardToken,
                            "installments", 1
                    ),
                    "reference", reference
            );
            HttpRequest txReq = HttpRequest.newBuilder()
                    .uri(URI.create(wompiConfig.getApiUrl() + "/transactions"))
                    .header("Authorization", "Bearer " + wompiConfig.getPrivateKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(txPayload)))
                    .timeout(Duration.ofSeconds(10))
                    .build();
            HttpResponse<String> txResp = httpClient.send(txReq, HttpResponse.BodyHandlers.ofString());
            JsonNode txJson = objectMapper.readTree(txResp.body());
            String wompiTxId = txJson.path("data").path("id").asText();
            String wompiStatus = txJson.path("data").path("status").asText();

            log.info("\n" +
                    "================================================================================\n" +
                    " [WOMPI CLOUD] ¡TRANSACCIÓN REAL CREADA EN WOMPI SANDBOX!\n" +
                    " ID Transacción Wompi: {}\n" +
                    " Referencia: {}\n" +
                    " Monto: ${} {}\n" +
                    " Estado: {}\n" +
                    " Comercio: Nexus Cultural (Wilson Ricardo Delgado Viveros)\n" +
                    " Portal Wompi para verificar: https://comercios.wompi.co/\n" +
                    "================================================================================",
                    wompiTxId, reference, montoTotal, currency, wompiStatus);

            return wompiTxId;
        } catch (Exception e) {
            log.warn("Excepción conectando a Wompi Cloud para transacción real: {}. Se usa fallback sandbox local.", e.getMessage());
            return "WOMPI-TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    /**
     * Crea los parámetros y la URL del Web Checkout de Wompi para un pedido específico.
     */
    @Transactional
    public WompiCheckoutSessionResponse crearSesionCheckout(UUID pedidoId, String redirectUrl) {
        Pedido pedido = pedidoRepository.obtenerPorId(pedidoId)
                .orElseThrow(() -> new ReglaDominioException("Pedido no encontrado: " + pedidoId));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            throw new ReglaDominioException("Solo se puede iniciar checkout para un pedido en estado PENDIENTE.");
        }

        BigDecimal montoTotal = pedido.getTotal().monto();
        long amountInCents = montoTotal.multiply(BigDecimal.valueOf(100)).longValue();
        String currency = pedido.getTotal().moneda() != null ? pedido.getTotal().moneda() : "COP";

        // Generamos una referencia única reconocible para la pasarela
        String reference = "NEXUS-" + pedido.getId().toString().substring(0, 8) + "-" + System.currentTimeMillis();

        String signature = generarFirmaIntegridad(reference, amountInCents, currency);

        String effectiveRedirectUrl = (redirectUrl != null && !redirectUrl.isBlank())
                ? redirectUrl
                : "https://nexuscultural.com/order-success?id=" + pedido.getId();

        String checkoutUrl = wompiConfig.getCheckoutUrl()
                + "?public-key=" + wompiConfig.getPublicKey()
                + "&currency=" + currency
                + "&amount-in-cents=" + amountInCents
                + "&reference=" + reference
                + "&signature:integrity=" + signature
                + "&redirect-url=" + URLEncoder.encode(effectiveRedirectUrl, StandardCharsets.UTF_8);

        return new WompiCheckoutSessionResponse(
                checkoutUrl,
                reference,
                wompiConfig.getPublicKey(),
                amountInCents,
                currency,
                signature,
                effectiveRedirectUrl,
                pedido.getId()
        );
    }

    /**
     * Confirma el pago de un pedido tras recibir la confirmación de Wompi (o simulación).
     * Si no trae transacción previa, crea la transacción real en los servidores de Wompi.
     */
    @Transactional
    public Pedido confirmarPago(ConfirmarPagoWompiRequest request) {
        Pedido pedido = pedidoRepository.obtenerPorId(request.idPedido())
                .orElseThrow(() -> new ReglaDominioException("Pedido no encontrado: " + request.idPedido()));

        if (pedido.getEstado() == EstadoPedido.PENDIENTE) {
            String transaccionId = request.transaccionId();
            if (transaccionId == null || transaccionId.isBlank() || transaccionId.startsWith("WOMPI-SIM-")) {
                transaccionId = crearTransaccionRealWompiCloud(pedido);
            }

            String metodo = (request.metodoPago() != null && !request.metodoPago().isBlank())
                    ? request.metodoPago()
                    : "WOMPI_CARD_SANDBOX";

            pedido.procesarPago(pedido.getTotal(), metodo, transaccionId);
            pedidoRepository.guardar(pedido);
            log.info("Pago confirmado con éxito en Wompi para pedido {}. Transacción: {}", pedido.getId(), transaccionId);
        }

        return pedido;
    }

    /**
     * Ejecuta la devolución / reembolso a través de Wompi (modo sandbox o simulación)
     * para un pedido cuya devolución fue aprobada.
     */
    @Transactional
    public String ejecutarReembolsoWompi(Pedido pedido) {
        String transaccionId = pedido.getReferenciaTransaccion();
        String refundReference = "WOMPI-REFUND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        if (transaccionId != null && !transaccionId.isBlank()) {
            try {
                // Intento de void / refund en la API de Wompi Sandbox
                String url = wompiConfig.getApiUrl() + "/transactions/" + transaccionId + "/void";
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + wompiConfig.getPrivateKey())
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .timeout(Duration.ofSeconds(10))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                log.info("\n" +
                        "================================================================================\n" +
                        " [WOMPI CLOUD] ¡ANULACIÓN / REEMBOLSO REGISTRADO EN WOMPI SANDBOX!\n" +
                        " ID Transacción Wompi: {}\n" +
                        " Código HTTP Respuesta: {}\n" +
                        " Respuesta Wompi: {}\n" +
                        " La transacción ahora aparecerá como VOIDED (Anulada) en https://comercios.wompi.co/\n" +
                        "================================================================================",
                        transaccionId, response.statusCode(), response.body());

                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    refundReference = "WOMPI-VOID-" + transaccionId;
                }
            } catch (Exception e) {
                log.warn("Excepción al contactar API Wompi para anulación: {}", e.getMessage());
            }
        } else {
            log.info("Transacción sin ID previo en Wompi para pedido {}. Reembolso simulado exitoso.", pedido.getId());
        }

        return refundReference;
    }

    /**
     * Obtiene el listado completo de pedidos con devolución activa o resuelta con detalles de comprador y obra.
     */
    public List<DevolucionDetalleResponse> listarDevolucionesDetalladas() {
        List<Pedido> pedidos = pedidoRepository.listarDevoluciones();
        List<DevolucionDetalleResponse> result = new ArrayList<>();

        for (Pedido p : pedidos) {
            String compradorNombre = "Comprador";
            try {
                Comprador c = compradorRepository.buscarPorId(p.getCompradorId()).orElse(null);
                if (c != null && c.getNombreCompleto() != null) {
                    compradorNombre = c.getNombreCompleto();
                }
            } catch (Exception ignored) {}

            String articuloTitulo = "Obra Cultural";
            try {
                Articulo a = articuloRepository.buscarPorId(p.getArticuloId()).orElse(null);
                if (a != null && a.getTitulo() != null) {
                    articuloTitulo = a.getTitulo();
                }
            } catch (Exception ignored) {}

            String motivo = p.getDevolucion() != null ? p.getDevolucion().getMotivo() : "Sin motivo registrado";
            String estadoDev = (p.getDevolucion() != null && p.getDevolucion().getEstado() != null)
                    ? p.getDevolucion().getEstado().name()
                    : "SOLICITADA";
            var fechaSol = p.getDevolucion() != null ? p.getDevolucion().getFechaSolicitud() : p.getFechaCreacion();

            result.add(new DevolucionDetalleResponse(
                    p.getId(),
                    p.getCompradorId(),
                    compradorNombre,
                    p.getArticuloId(),
                    articuloTitulo,
                    p.getCantidad(),
                    p.getTotal() != null ? p.getTotal().monto() : BigDecimal.ZERO,
                    p.getTotal() != null ? p.getTotal().moneda() : "COP",
                    motivo,
                    estadoDev,
                    fechaSol,
                    p.getMetodoPago(),
                    p.getReferenciaTransaccion()
            ));
        }

        return result;
    }
}
