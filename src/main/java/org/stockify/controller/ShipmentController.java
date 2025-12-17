package org.stockify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.shipment.ShipmentFilterRequest;
import org.stockify.dto.request.shipment.UpdateShipmentRequest;
import org.stockify.dto.response.ShipmentResponse;
import org.stockify.dto.response.SaleResponse;
import org.stockify.model.assembler.ShipmentModelAssembler;
import org.stockify.model.service.ShipmentService;

import java.util.List;

@RestController
@RequestMapping("/shipments")
@Validated
@Tag(name = "Shipments", description = "Endpoints for managing Shipments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ShipmentController {
    private final ShipmentService shipmentService;
    private final ShipmentModelAssembler shipmentModelAssembler;

    @Operation(
            summary = "Get paged list of shipments",
            description = "Returns a paginated list of shipments, optionally filtered",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Paged list of shipments retrieved",
                    content = @Content(schema = @Schema(implementation = PagedModel.class))
                )
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('READ')")
    public ResponseEntity<PagedModel<EntityModel<ShipmentResponse>>> getAll(
            @Parameter(description = "Filter request object")
            @ParameterObject ShipmentFilterRequest filterRequest,

            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size,
            PagedResourcesAssembler<ShipmentResponse> assembler) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<ShipmentResponse> saleResponsePage = shipmentService.findAll(filterRequest, pageable);

        return ResponseEntity.ok(assembler.toModel(saleResponsePage, shipmentModelAssembler));
    }

    @Operation(
            summary = "Get shipment by ID",
            description = "Returns a single shipment by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shipment found",
                            content = @Content(schema = @Schema(implementation = SaleResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Shipment not found", content = @Content)
            }
    )
    @GetMapping("/{shipmentID}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER') or hasAuthority('READ')")
    public ResponseEntity<EntityModel<ShipmentResponse>> getOrderById(
            @Parameter(description = "Order ID", required = true, example = "1")
            @PathVariable Long shipmentID) {
        ShipmentResponse shipmentResponse = shipmentService.findById(shipmentID);
        return ResponseEntity.ok(shipmentModelAssembler.toModel(shipmentResponse));
    }

    @Operation(summary = "Get Shipments for a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shipments returned successfully"),
            @ApiResponse(responseCode = "404", description = "Shipments or user not found")
    })
    @GetMapping("/my-shipments/{userID}")
    public ResponseEntity<List<ShipmentResponse>> findOrdersByUser(
            @Parameter(description = "Identifier of the user who owns the shipments", example = "12")
            @PathVariable("userID") Long userId) {
        return ResponseEntity.ok(shipmentService.findOrdersByUser(userId));
    }

    @Operation(
            summary = "Delete a shipment by ID",
            description = "Deletes a shipment given its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Shipment deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Shipment not found", content = @Content)
            }
    )
    @DeleteMapping("/{shipmentID}")
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAuthority('DELETE') or " +
            "hasRole('ROLE_MANAGER') and hasAuthority('DELETE')")
    public ResponseEntity<Void> deleteOrderById(
            @Parameter(description = "Order ID", required = true, example = "1")
            @PathVariable Long shipmentID) {
        shipmentService.delete(shipmentID);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Partially update a shipment by ID",
            description = "Performs partial update of a shipment given its ID",
            requestBody = @RequestBody(
                    description = "Partial shipment update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateShipmentRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Shipment partially updated successfully",
                            content = @Content(schema = @Schema(implementation = ShipmentResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Shipment not found", content = @Content)
            }
    )
    @PatchMapping("/{shipmentID}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<EntityModel<ShipmentResponse>> patchOrder(
            @Parameter(description = "Shipment ID", required = true, example = "1")
            @PathVariable Long shipmentID,

            @Parameter(description = "Shipment request body", required = true)
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateShipmentRequest shipmentsRequest) {
        ShipmentResponse updatedOrder = shipmentService.updateOrderPartial(shipmentID, shipmentsRequest);
        EntityModel<ShipmentResponse> entityModel = shipmentModelAssembler.toModel(updatedOrder);
        return ResponseEntity.ok(entityModel);
    }
}
