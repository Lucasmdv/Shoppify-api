package org.stockify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.user.UserFilterRequest;
import org.stockify.dto.request.user.UserRequest;
import org.stockify.dto.response.UserResponse;
import org.stockify.model.assembler.UserModelAssembler;
import org.stockify.model.service.UserService;
 

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {

    private final UserService clientService;
    private final UserModelAssembler clientModelAssembler;


    @Operation(summary = "List all users with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paged list of clients retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<UserResponse>>> getAllUsers(
            @ParameterObject UserFilterRequest filterRequest,
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            PagedResourcesAssembler<UserResponse> pagedAssembler) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<UserResponse> clientResponsePage = clientService.findAll(filterRequest, pageable);

        return ResponseEntity.ok(pagedAssembler.toModel(clientResponsePage, clientModelAssembler));
    }

    @Operation(summary = "Get client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client found"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{clientID}")
    public ResponseEntity<EntityModel<UserResponse>> getUserById(
            @Parameter(description = "ID of the client") @PathVariable Long clientID) {

        UserResponse clientResponse = clientService.findById(clientID);
        return ResponseEntity.ok(clientModelAssembler.toModel(clientResponse));
    }

    @Operation(summary = "Delete a client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @DeleteMapping("/{clientID}")
    public ResponseEntity<Void> deleteUserById(
            @Parameter(description = "ID of the client") @PathVariable Long clientID) {

        clientService.delete(clientID);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Partially update a client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PatchMapping("/{clientID}")
    public ResponseEntity<EntityModel<UserResponse>> patchUser(
            @Parameter(description = "ID of the client") @PathVariable Long clientID,
            @RequestBody UserRequest client) {

        UserResponse updatedClient = clientService.updateClientPartial(clientID, client);
        return ResponseEntity.ok(clientModelAssembler.toModel(updatedClient));
    }

    @Operation(summary = "Fully update a client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PutMapping("/{clientID}")
    public ResponseEntity<EntityModel<UserResponse>> putUser(
            @Parameter(description = "ID of the client") @PathVariable Long clientID,
            @Valid @RequestBody UserRequest client) {

        UserResponse updatedClient = clientService.updateClientFull(clientID, client);
        return ResponseEntity.ok(clientModelAssembler.toModel(updatedClient));
    }
}
