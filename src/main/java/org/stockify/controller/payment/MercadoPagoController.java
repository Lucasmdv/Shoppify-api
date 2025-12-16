package org.stockify.controller.payment;

import com.mercadopago.resources.preference.Preference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.response.MercadoPagoPreferenceResponse;
import org.stockify.model.service.MercadoPagoService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/mercadopago")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Mercado Pago", description = "Endpoints for Mercado Pago integration")
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

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
                preference.getSandboxInitPoint(),
                Long.valueOf(preference.getExternalReference()));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Webhook para notificaciones de pago de MercadoPago", description = "Endpoint que recibe las notificaciones de pago de MercadoPago")
    @PostMapping("/webhook")
    public ResponseEntity<String> webhookNotification(
            @RequestParam Map<String, String> params,
            @RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "x-signature", required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId) {
        return mercadoPagoService.handleWebhook(params, body, xSignature, xRequestId);
    }
}
