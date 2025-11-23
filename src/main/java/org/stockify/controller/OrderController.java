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
import org.stockify.dto.request.order.OrderFilterRequest;
import org.stockify.dto.request.order.UpdateOrderRequest;
import org.stockify.dto.response.OrderResponse;
import org.stockify.dto.response.SaleResponse;
import org.stockify.model.assembler.OrderModelAssembler;
import org.stockify.model.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Validated
@Tag(name = "Orders", description = "Endpoints for managing Orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class OrderController {
    private final OrderService orderService;
    private final OrderModelAssembler orderModelAssembler;

    @Operation(
            summary = "Get paged list of orders",
            description = "Returns a paginated list of orders, optionally filtered",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Paged list of orders retrieved",
                    content = @Content(schema = @Schema(implementation = PagedModel.class))
                )
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<PagedModel<EntityModel<OrderResponse>>> getAll(
            @Parameter(description = "Filter request object")
            @ParameterObject OrderFilterRequest filterRequest,

            @Parameter(description = "Page number (0..N)", example = "0")
            @RequestParam(required = false, defaultValue = "0") int page,

            @Parameter(description = "Page size", example = "20")
            @RequestParam(required = false, defaultValue = "20") int size,
            PagedResourcesAssembler<OrderResponse> assembler) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<OrderResponse> saleResponsePage = orderService.findAll(filterRequest, pageable);

        return ResponseEntity.ok(assembler.toModel(saleResponsePage, orderModelAssembler));
    }

    @Operation(
            summary = "Get order by ID",
            description = "Returns a single order by its ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Sale found",
                            content = @Content(schema = @Schema(implementation = SaleResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Sale not found", content = @Content)
            }
    )
    @GetMapping("/{orderID}")
    @PreAuthorize("hasAuthority('READ')")
    public ResponseEntity<EntityModel<OrderResponse>> getOrderById(
            @Parameter(description = "Order ID", required = true, example = "1")
            @PathVariable Long orderID) {
        OrderResponse orderResponse = orderService.findById(orderID);
        return ResponseEntity.ok(orderModelAssembler.toModel(orderResponse));
    }

    @Operation(summary = "Get Orders for a specific user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Orders returned successfully"),
            @ApiResponse(responseCode = "404", description = "Orders or user not found")
    })
    @GetMapping("/my-orders/{userID}")
    public ResponseEntity<List<OrderResponse>> findOrdersByUser(
            @Parameter(description = "Identifier of the user who owns the orders", example = "12")
            @PathVariable("userID") Long userId) {
        return ResponseEntity.ok(orderService.findOrdersByUser(userId));
    }

    @Operation(
            summary = "Delete a order by ID",
            description = "Deletes a order given its ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
            }
    )
    @DeleteMapping("/{orderID}")
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAuthority('DELETE') or " +
            "hasRole('ROLE_MANAGER') and hasAuthority('DELETE')")
    public ResponseEntity<Void> deleteOrderById(
            @Parameter(description = "Order ID", required = true, example = "1")
            @PathVariable Long orderID) {
        orderService.delete(orderID);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Partially update a order by ID",
            description = "Performs partial update of a order given its ID",
            requestBody = @RequestBody(
                    description = "Partial order update request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateOrderRequest.class))
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order partially updated successfully",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
            }
    )
    @PatchMapping("/{orderID}")
    @PreAuthorize("hasRole('ROLE_ADMIN') and hasAuthority('WRITE') or " +
            "hasRole('ROLE_MANAGER') and hasAuthority('WRITE')")
    public ResponseEntity<EntityModel<OrderResponse>> patchOrder(
            @Parameter(description = "Order ID", required = true, example = "1")
            @PathVariable Long orderID,

            @Parameter(description = "Order request body", required = true)
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateOrderRequest orderRequest) {
        OrderResponse updatedOrder = orderService.updateOrderPartial(orderID, orderRequest);
        EntityModel<OrderResponse> entityModel = orderModelAssembler.toModel(updatedOrder);
        return ResponseEntity.ok(entityModel);
    }
}