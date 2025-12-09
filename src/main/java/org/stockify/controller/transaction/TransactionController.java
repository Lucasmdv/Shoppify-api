package org.stockify.controller.transaction;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.transaction.TransactionCreatedRequest;
import org.stockify.dto.response.TransactionResponse;
import org.stockify.model.enums.TransactionType;
import org.stockify.model.service.TransactionService;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Operations related to generic transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Create a generic transaction (type = OTHER)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/transactions")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('WRITE')")
    public ResponseEntity<TransactionResponse> createTransaction(
            @Parameter(description = "Transaction payload") @RequestBody @Valid TransactionCreatedRequest request) {
        return ResponseEntity.ok(transactionService.saveTransaction(request, TransactionType.OTHER));
    }
}
