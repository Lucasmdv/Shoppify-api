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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.response.MercadoPagoPreferenceResponse;
import org.stockify.model.service.MercadoPagoService;

@RestController
@RequestMapping("/mercadopago")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Mercado Pago", description = "Endpoints for Mercado Pago integration")
public class MercadoPagoController {

    private final MercadoPagoService mercadoPagoService;

    @Operation(
            summary = "Create a Mercado Pago preference",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Preference created", content = @Content(schema = @Schema(implementation = MercadoPagoPreferenceResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid payload", content = @Content)
            }
    )
    @PostMapping("/preferences")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MercadoPagoPreferenceResponse> createPreference(@Valid @RequestBody SaleRequest request) {
        Preference preference = mercadoPagoService.createPreference(request);
        MercadoPagoPreferenceResponse response = new MercadoPagoPreferenceResponse(
                preference.getId(),
                preference.getInitPoint(),
                preference.getSandboxInitPoint()
        );
        return ResponseEntity.ok(response);
    }
}
