package org.stockify.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.stockify.dto.request.user.UserFilterRequest;
import org.stockify.dto.request.user.UserRequest;
import org.stockify.dto.response.UserResponse;
import org.stockify.model.assembler.UserModelAssembler;
import org.stockify.model.service.UserService;
import org.stockify.security.model.dto.request.AuthRequest;
import org.stockify.security.model.dto.response.AuthResponse;

import java.net.URI;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {

    private final UserService clientService;
    private final UserModelAssembler clientModelAssembler;

    @Operation(summary = "Create a new client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PreAuthorize("hasRole('ROLE_MANAGER') and hasAuthority('WRITE') or " +
            "hasRole('ROLE_ADMIN') and hasAuthority('WRITE')")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<EntityModel<UserResponse>> createClient(
            @Valid @RequestBody UserRequest client) {

        UserResponse clientResponse = clientService.save(client);
        EntityModel<UserResponse> entityModel = clientModelAssembler.toModel(clientResponse);

        return ResponseEntity
                .created(URI.create(entityModel.getRequiredLink("self").getHref()))
                .body(entityModel);
    }

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

    @PostMapping("/create")
    @Operation(
            summary = "Create user with edit permits",
            description = "Creates a new user with edit permits and returns a JWT token for authenticated requests"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    public ResponseEntity<AuthResponse> createUser(
            @Parameter(description = "Authentication credentials") @RequestBody AuthRequest authRequest) {
        UserDetails user = clientService.createUser(authRequest);
        String token = clientService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/edit")
    @Operation(
            summary = "Edit permits from a user beign admin and get token",
            description = "Validates user credentials and returns a JWT token for authenticated requests"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials"),
            @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    public ResponseEntity<AuthResponse> editPermitsUser(
            @Parameter(description = "Authentication credentials") @RequestBody AuthRequest authRequest) {
        UserDetails user = clientService.editPermitsUser(authRequest);
        String token = clientService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }


    //Esto es la combinacion del createClient y el createUser
    //Crea el usuario y las credenciales en una sola llamada
    //No necesita autenticacion porque es para el registro. fue pensado para un usuario de un Ecommerce
    @PostMapping("/register")
    @Operation(
        summary = "Complete user registration",
        description = "Registers a new user with both profile data and authentication credentials"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseEntity<EntityModel<UserResponse>> registerUser(
            @Valid @RequestBody UserRequest userRequest,
            @RequestParam String email,
            @RequestParam String password) {

        // Registra credenciales (autenticación)
        clientService.createUser(new AuthRequest(email, password));

        // Registra datos de usuario
        UserResponse userResponse = clientService.save(userRequest);
        EntityModel<UserResponse> entityModel = clientModelAssembler.toModel(userResponse);

        return ResponseEntity
                .created(URI.create(entityModel.getRequiredLink("self").getHref()))
                .body(entityModel);
    }
}
