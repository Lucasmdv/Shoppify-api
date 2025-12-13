package org.stockify.controller.payment;

import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stockify.config.MercadoPagoIntegrationConfig;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.response.MercadoPagoPreferenceResponse;
import org.stockify.model.service.MercadoPagoService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mercadopago")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Mercado Pago", description = "Endpoints for Mercado Pago integration")
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;
    private final MercadoPagoIntegrationConfig mercadoPagoConfig;

    @Operation(summary = "Create a Mercado Pago preference", responses = {
            @ApiResponse(responseCode = "200", description = "Preference created", content = @Content(schema = @Schema(implementation = MercadoPagoPreferenceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content)
    })
    @PostMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MercadoPagoPreferenceResponse> createPreference(@Valid @RequestBody SaleRequest request) {
        Preference preference = mercadoPagoService.createPreference(request);
        
        MercadoPagoPreferenceResponse response = new MercadoPagoPreferenceResponse(
                preference.getId(),
                preference.getInitPoint(),
                preference.getSandboxInitPoint());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Webhook para notificaciones de pago de MercadoPago", description = "Endpoint que recibe las notificaciones de pago de MercadoPago")
    @PostMapping("/webhook")
    public ResponseEntity<String> webhookNotification(
            @RequestParam Map<String, String> params,
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId,
            HttpServletRequest request) {

        if (xSignature == null || xRequestId == null) {
            log.warn("Missing signature headers");
        }

        String dataId = params.get("data.id");

        // 1. Validate Signature
        if (xSignature != null && dataId != null) {
            if (!isValidSignature(xSignature, dataId, xRequestId)) {
                log.warn("Invalid Webhook Signature. Potential spoofing attempt. ID: {}", dataId);
                return ResponseEntity.status(401).body("Invalid Signature");
            }
        }
        try {
            String topic = params.get("type");
            String paymentId = params.get("data.id");

            log.info("Notificación recibida - Tipo: {}, ID de pago: {}", topic, paymentId);

            // Procesar la notificación según el tipo
            if (paymentId != null) {
                try {
                    mercadoPagoService.processWebhookNotification(topic, paymentId);
                    return ResponseEntity.ok("Notificación recibida correctamente");
                } catch (RuntimeException e) {
                    // Si el error es negocio (no encontrado, etc), devolvemos OK o BAD REQUEST para
                    // que MP no reintente
                    if (e.getMessage().contains("No se pudo encontrar el pago")
                            || e.getMessage().contains("Payment not found")) {
                        log.warn("Pago no encontrado o inválido, deteniendo reintentos: {}", e.getMessage());
                        return ResponseEntity.ok("Notificación procesada (Pago no encontrado)");
                    }
                    throw e; // Re-lanzar para que caiga en el catch general si es error inesperado
                }
            }

            return ResponseEntity.badRequest().body("ID de pago no proporcionado");
        } catch (Exception e) {
            log.error("Error al procesar notificación de webhook", e);
            return ResponseEntity.status(500).body("Error al procesar la notificación");
        }
    }

    private boolean isValidSignature(String xSignature, String dataId, String requestId) {
        try {
            // xSignature format: ts=...,v1=...
            String[] parts = xSignature.split(",");
            String ts = null;
            String v1 = null;

            for (String part : parts) {
                if (part.startsWith("ts="))
                    ts = part.substring(3);
                if (part.startsWith("v1="))
                    v1 = part.substring(3);
            }

            if (ts == null || v1 == null)
                return false;

            String secret = mercadoPagoConfig.getWebhookSecret();
            if (secret == null || secret.isEmpty()) {
                log.warn("Webhook secret not configured. Skipping validation.");
                return true;
            }

            // Construct manifest string for HMAC verification
            String manifest = String.format("id:%s;request-url:%s;ts:%s;params:%s",
                    dataId,
                    requestId,
                    ts,
                    "type=payment");

            String generatedSignature = hmacSha256(mercadoPagoConfig.getWebhookSecret(), manifest);
            return generatedSignature.equals(v1);

        } catch (Exception e) {
            log.error("Error validating signature", e);
            return false;
        }
    }

    private String hmacSha256(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] signedBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : signedBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
