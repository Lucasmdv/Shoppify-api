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

    private final UserService userService;
    private final UserModelAssembler userModelAssembler;


    @Operation(summary = "List all users with optional filters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Paged list of users retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<UserResponse>>> getAllUsers(
            @ParameterObject UserFilterRequest filterRequest,
            @Parameter(description = "Page number", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "20") @RequestParam(defaultValue = "20") int size,
            PagedResourcesAssembler<UserResponse> pagedAssembler) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        Page<UserResponse> userResponsePage = userService.findAll(filterRequest, pageable);

        return ResponseEntity.ok(pagedAssembler.toModel(userResponsePage, userModelAssembler));
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{userID}")
    public ResponseEntity<EntityModel<UserResponse>> getUserById(
            @Parameter(description = "ID of the user") @PathVariable Long userID) {

        UserResponse userResponse = userService.findById(userID);
        return ResponseEntity.ok(userModelAssembler.toModel(userResponse));
    }

    @Operation(summary = "Delete a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{userID}")
    public ResponseEntity<Void> deleteUserById(
            @Parameter(description = "ID of the user") @PathVariable Long userID) {

        userService.delete(userID);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Partially update a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/{userID}")
    public ResponseEntity<EntityModel<UserResponse>> patchUser(
            @Parameter(description = "ID of the user") @PathVariable Long userID,
            @RequestBody UserRequest user) {

        UserResponse updatedUser = userService.updateUserPartial(userID, user);
        return ResponseEntity.ok(userModelAssembler.toModel(updatedUser));
    }

    @Operation(summary = "Fully update a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{userID}")
    public ResponseEntity<EntityModel<UserResponse>> putUser(
            @Parameter(description = "ID of the user") @PathVariable Long userID,
            @Valid @RequestBody UserRequest user) {

        UserResponse updatedUser = userService.updateUserFull(userID, user);
        return ResponseEntity.ok(userModelAssembler.toModel(updatedUser));
    }
}
