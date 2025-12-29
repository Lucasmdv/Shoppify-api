package org.stockify.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.stockify.security.model.enums.Permit;

import java.util.List;

@RestController
@RequestMapping("/permits")
@Tag(name = "Permits", description = "List available permits (enum)")
public class PermitController {

    @GetMapping
    @Operation(summary = "List all permits")
    public ResponseEntity<List<Permit>> list() {
        return ResponseEntity.ok(List.of(Permit.values()));
    }
}
