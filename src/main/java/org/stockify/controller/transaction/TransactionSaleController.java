package org.stockify.controller.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.stockify.security.model.entity.CredentialsEntity;
import org.springframework.security.core.Authentication;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.response.SaleResponse;
import org.stockify.model.assembler.SaleModelAssembler;
import org.stockify.model.service.SaleService;

@RestController
@RequestMapping("/transactions/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Endpoints for managing sales transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionSaleController {

        private final SaleService saleService;
        private final SaleModelAssembler saleModelAssembler;

        @Operation(
                summary = "Create a new sale",
                description = "Creates a sale without requiring a store context")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Sale created successfully",
                                content = @Content(schema = @Schema(implementation = SaleResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
        })
        @PostMapping
        @PreAuthorize("isAuthenticated()")
        public ResponseEntity<EntityModel<SaleResponse>> create(
                        @Parameter(description = "Sale request body", required = true)
                        @Valid
                        @RequestBody SaleRequest request,Authentication authentication) {
                CredentialsEntity credentials = (CredentialsEntity) authentication.getPrincipal();
                request.setUserId(credentials.getUser().getId());

                SaleResponse saleResponse = saleService.createSale(request);
                EntityModel<SaleResponse> entityModel = saleModelAssembler.toModel(saleResponse);

                return ResponseEntity
                                .created(entityModel.getRequiredLink("self").toUri())
                                .body(entityModel);
        }
}
