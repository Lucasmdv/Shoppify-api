package org.stockify.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stockify.security.model.dto.request.RoleAssignRequest;
import org.stockify.security.model.dto.request.RoleCreateRequest;
import org.stockify.security.model.entity.CredentialsEntity;
import org.stockify.security.model.entity.RoleEntity;
import org.stockify.security.model.enums.Permit;
import org.stockify.security.service.RoleService;

import java.util.Set;

@RestController
@RequestMapping("/roles")
@Tag(name = "Roles & Permits", description = "Endpoints to manage roles and assigns")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @Operation(summary = "Create a dynamic role (optional permits)")
    public ResponseEntity<RoleEntity> createRole(@RequestBody RoleCreateRequest request) {
        RoleEntity created = roleService.createRole(request);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}/permits")
    @Operation(summary = "Set role permits in bulk by role id (replace)")
    public ResponseEntity<RoleEntity> setRolePermits(@PathVariable Long id, @RequestBody Set<Permit> permits) {
        RoleEntity updated = roleService.setRolePermitsById(id, permits);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/assign")
    @Operation(summary = "Assign a role to a user by role id (add-only)")
    public ResponseEntity<CredentialsEntity> assignRole(@RequestBody RoleAssignRequest request) {
        CredentialsEntity updated = roleService.assignRoleToUserById(request.getEmail(), request.getRoleId());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/{email}/set")
    @Operation(summary = "Set role to user by role id (permissions handled separately)")
    public ResponseEntity<CredentialsEntity> setRoleToUser(
            @PathVariable String email,
            @RequestBody RoleAssignRequest body
    ) {
        CredentialsEntity updated = roleService.assignRoleToUserById(email, body.getRoleId());
        return ResponseEntity.ok(updated);
    }
}
