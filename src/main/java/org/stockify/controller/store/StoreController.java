package org.stockify.controller.store;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.store.StoreRequest;
import org.stockify.dto.response.StoreResponse;
import org.stockify.model.assembler.StoreModelAssembler;
import org.stockify.model.service.StoreService;

import java.net.URI;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
@Tag(name = "Stores", description = "Operations related to store management")
@SecurityRequirement(name = "bearerAuth")
public class StoreController {

    private static final long STORE_ID = 1L;

    private final StoreService storeService;
    private final StoreModelAssembler storeModelAssembler;


    @Operation(summary = "Get singleton store")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Store found"),
            @ApiResponse(responseCode = "404", description = "Store not found")
    })
    @GetMapping("/singleton")
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAuthority('READ') or " +
            "hasRole('ROLE_MANAGER') and hasAuthority('READ')")
    public ResponseEntity<EntityModel<StoreResponse>> getStore() {
        StoreResponse store = storeService.findById(STORE_ID);
        return ResponseEntity.ok(storeModelAssembler.toModel(store));
    }

    @Operation(summary = "Update singleton store")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Store updated successfully"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PutMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAuthority('WRITE')")
    public ResponseEntity<EntityModel<StoreResponse>> updateStore(
            @Valid @RequestBody StoreRequest request) {
        StoreResponse store = storeService.update(STORE_ID, request);
        return ResponseEntity.ok(storeModelAssembler.toModel(store));
    }
}
