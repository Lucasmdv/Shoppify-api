package org.stockify.security.service;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.stockify.security.exception.AuthenticationException;
import org.stockify.security.model.dto.request.RoleAndPermitsDTO;
import org.stockify.security.model.entity.CredentialsEntity;
import org.stockify.security.model.entity.PermitEntity;
import org.stockify.security.model.entity.RoleEntity;
import org.stockify.security.model.enums.Permit;
import org.stockify.security.model.enums.Role;
import org.stockify.security.repository.CredentialRepository;
import org.stockify.security.model.dto.request.AuthRequest;
import org.stockify.security.repository.PermitRepository;
import org.stockify.security.repository.RolRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service responsible for authentication and user management operations.
 * Handles user registration, authentication, role and permission management.
 */
@Transactional
@Service
public class AuthService {

    /**
     * Repository for user credentials
     */
    private final CredentialRepository credentialsRepository;

    /**
     * Spring Security authentication manager
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Password encoder for secure password storage
     */
    private final PasswordEncoder passwordEncoder;


    /**
     * Service for JWT token operations
     */
    private final JwtService jwtService;

    /**
     * Repository for permission data
     */
    private final PermitRepository permitRepository;

    /**
     * Repository for role data
     */
    private final RolRepository rolRepository;

    /**
     * Constructor for AuthService
     *
     * @param credentialsRepository Repository for user credentials
     * @param authenticationManager Spring Security authentication manager
     * @param passwordEncoder Password encoder for secure password storage
     * @param jwtService Service for JWT token operations
     * @param permitRepository Repository for permission data
     * @param rolRepository Repository for role data
     */
    public AuthService(CredentialRepository credentialsRepository,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       PermitRepository permitRepository,
                       RolRepository rolRepository) {
        this.credentialsRepository = credentialsRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.permitRepository = permitRepository;
        this.rolRepository = rolRepository;
    }

    /**
     * Creates or updates roles and permissions for a user.
     *
     * @param email Email of the user
     * @param role Role to assign
     * @param permits Permissions to assign
     * @return The updated credentials
     * @throws UsernameNotFoundException If no user is found with the provided email
     */
    public CredentialsEntity updateUserRoleAndPermits(String email, Role role, Set<Permit> permits) {
        // Get current user's authorities to check if they are admin or manager
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isManager = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"));

        // If the current user is a MANAGER and trying to create an ADMIN account, throw an exception
        if (isManager && !isAdmin && role == Role.ADMIN) {
            throw new AuthenticationException("MANAGER role cannot create ADMIN accounts", null);
        }

        // Process permissions
        Set<PermitEntity> permitEntities = new HashSet<>();
        for (Permit p : permits) {
            // Find or create the permission
            PermitEntity permitEntity = permitRepository.findByPermit(p)
                    .orElseGet(() -> {
                        PermitEntity newPermit = PermitEntity.builder()
                                .permit(p)
                                .build();
                        return permitRepository.save(newPermit);
                    });
            permitEntities.add(permitEntity);
        }

        // Find or create the role
        RoleEntity roleEntity = rolRepository.findByRole(role)
                .orElseGet(() -> {
                    RoleEntity newRole = RoleEntity.builder()
                            .role(role)
                            .permits(permitEntities)
                            .build();
                    return rolRepository.save(newRole);
                });

        // Update the credentials
        CredentialsEntity credentials = credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Set<RoleEntity> roles = credentials.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        } else {
            roles = new HashSet<>(roles);
        }

        roles.add(roleEntity);
        credentials.setRoles(roles);
        return credentialsRepository.save(credentials);
    }

    /**
     * Updates the roles and permissions for a user identified by email.
     * If the requested role or permissions don't exist, they will be created.
     *
     * @param email Email of the user to update
     * @param roleAndPermitsDTO DTO containing the roles and permissions to assign
     * @return Response containing the updated user information
     * @throws UsernameNotFoundException If no user is found with the provided email
     */
    public ResponseEntity<CredentialsEntity> setPermitsAndRole(String email, RoleAndPermitsDTO roleAndPermitsDTO) {
        CredentialsEntity credentials = credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        Set<Permit> requestedPermits = roleAndPermitsDTO.getPermits();
        Set<PermitEntity> permitEntities = new HashSet<>();

        for (Permit permit : requestedPermits) {
            PermitEntity permitEntity = permitRepository.findByPermit(permit)
                    .orElseGet(() -> permitRepository.save(
                            PermitEntity.builder().permit(permit).build()
                    ));
            permitEntities.add(permitEntity);
        }

        Role requestedRole = roleAndPermitsDTO.getRoles();
        RoleEntity roleEntity = rolRepository.findByRole(requestedRole)
                .orElseGet(() -> rolRepository.save(
                        RoleEntity.builder()
                                .role(requestedRole)
                                .permits(permitEntities)
                                .build()
                ));

        // Si el rol ya existía pero no tiene estos permisos, actualizarlos:
        if (!roleEntity.getPermits().containsAll(permitEntities)) {
            roleEntity.getPermits().addAll(permitEntities);
            rolRepository.save(roleEntity);
        }

        Set<RoleEntity> rolesActuales = credentials.getRoles();

        if (rolesActuales == null) {
            rolesActuales = new HashSet<>();
        } else {
            // En caso que sea un Set inmutable, lo hacemos mutable
            rolesActuales = new HashSet<>(rolesActuales);
        }

        rolesActuales.add(roleEntity);
        credentials.setRoles(rolesActuales);
        credentialsRepository.save(credentials);

        return ResponseEntity.ok(credentials);
    }

    /**
     * Creates or retrieves the standard set of permissions for regular employees.
     * This includes READ and WRITE permissions.
     *
     * @return List of permission entities for regular employees
     */
    public List<PermitEntity> permitUser(){
        List<PermitEntity> permits = new ArrayList<>();

        // Get or create READ permit
        PermitEntity readPermit = permitRepository.findByPermit(Permit.READ)
                .orElseGet(() -> {
                    PermitEntity newPermit = PermitEntity.builder().permit(Permit.READ).build();
                    return permitRepository.save(newPermit);
                });
        permits.add(readPermit);

        // Get or create WRITE permit
        PermitEntity writePermit = permitRepository.findByPermit(Permit.WRITE)
                .orElseGet(() -> {
                    PermitEntity newPermit = PermitEntity.builder().permit(Permit.WRITE).build();
                    return permitRepository.save(newPermit);
                });
        permits.add(writePermit);

        return permits;
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param input Authentication request containing email and password
     * @return UserDetails of the authenticated user
     * @throws AuthenticationException If authentication fails
     * @throws UsernameNotFoundException If no user is found with the provided email
     */
    public UserDetails authenticate(AuthRequest input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.email(),
                            input.password()
                    )
            );
            CredentialsEntity credentials = credentialsRepository.findByEmail(input.email())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + input.email()));

            return credentials;
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Invalid email or password", e);
        } catch (Exception e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage(), e);
        }
    }

    /**
     * Logs out the currently authenticated user.
     * Invalidates the JWT token.
     *
     * @throws UsernameNotFoundException If no user is found with the extracted email
     */
    public void logout() {
        String token = jwtService.extractTokenFromSecurityContext();
        String email = jwtService.extractUsername(token);
        credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        jwtService.invalidateToken(token);
    }

    public CredentialsEntity getAuthenticatedUser() {
        String token = jwtService.extractTokenFromSecurityContext();
        String userEmail = jwtService.extractUsername(token);
        return credentialsRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
