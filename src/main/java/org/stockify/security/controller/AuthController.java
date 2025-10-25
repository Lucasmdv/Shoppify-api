package org.stockify.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.stockify.security.model.dto.request.AuthRequest;
import org.stockify.security.model.dto.response.AuthResponse;
import org.stockify.security.model.dto.response.LoginResponse;
import org.stockify.security.model.dto.request.RegisterRequest;
import org.stockify.security.model.dto.response.RegisterResponse;
import org.stockify.dto.response.UserResponse;
import org.stockify.model.mapper.UserMapper;

import org.stockify.security.service.AuthService;
import org.stockify.security.service.JwtService;

/**
 * REST controller for authentication operations.
 * Provides endpoints for user login and logout.
 */

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Operations for user authentication")
public class AuthController {

    /**
     * Service for authentication operations
     */
    private final AuthService authService;

    /**
     * Service for JWT token operations
     */
    private final JwtService jwtService;
    private final UserMapper userMapper;

    /**
     * Constructor for AuthController
     *
     * @param authService Service for authentication operations
     * @param jwtService Service for JWT token operations
     */
    public AuthController(AuthService authService, JwtService jwtService, UserMapper userMapper) {
        this.authService = authService;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    /**
     * Logs out the currently authenticated user
     *
     * @return A success message
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Logout the current user",
        description = "Invalidates the current user's session and JWT token",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("Logout successful");
    }


    /**
     * Authenticates a user and generates a JWT token
     *
     * @param authRequest Request containing username and password
     * @return Response containing the JWT token
     */
    @PostMapping("/login")
    @Operation(
        summary = "Authenticate user and get token",
        description = "Validates user credentials and returns a JWT token for authenticated requests"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Authentication successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @ApiResponse(responseCode = "400", description = "Invalid request format")
    })
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "Authentication credentials") @RequestBody AuthRequest authRequest) {
        UserDetails userDetails = authService.authenticate(authRequest);
        String token = jwtService.generateToken(userDetails);
        org.stockify.security.model.entity.CredentialsEntity cred = (org.stockify.security.model.entity.CredentialsEntity) userDetails;
        org.stockify.dto.response.UserResponse profile = userMapper.toDto(cred.getUser());
        // Sanitize potentially sensitive fields for login payload
        profile.setDni(null);
        profile.setPhone(null);
        java.util.Set<String> permits = cred.getRoles().stream()
                .filter(java.util.Objects::nonNull)
                .map(org.stockify.security.model.entity.RoleEntity::getPermits)
                .filter(java.util.Objects::nonNull)
                .flatMap(java.util.Set::stream)
                .filter(java.util.Objects::nonNull)
                .map(p -> p.getPermit())
                .filter(java.util.Objects::nonNull)
                .map(Enum::name)
                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));
        return ResponseEntity.ok(
                LoginResponse.builder()
                        .token(token)
                        .user(profile)
                        .permits(permits)
                        .build()
        );
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register user profile and credentials",
        description = "Registers a new user profile and credentials in one step and assigns default CLIENT role"
    )
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        System.out.println(request);
        org.stockify.security.model.entity.CredentialsEntity saved = authService.register(request);
        String token = jwtService.generateToken(saved);
        UserResponse profile = userMapper.toDto(saved.getUser());
        java.util.Set<String> permits = saved.getRoles().stream()
                .filter(java.util.Objects::nonNull)
                .map(org.stockify.security.model.entity.RoleEntity::getPermits)
                .filter(java.util.Objects::nonNull)
                .flatMap(java.util.Set::stream)
                .filter(java.util.Objects::nonNull)
                .map(p -> p.getPermit())
                .filter(java.util.Objects::nonNull)
                .map(Enum::name)
                .collect(java.util.stream.Collectors.toCollection(java.util.TreeSet::new));
        return ResponseEntity.status(201).body(
                RegisterResponse.builder()
                        .token(token)
                        .user(profile)
                        .permits(permits)
                        .build()
        );
    }
}
