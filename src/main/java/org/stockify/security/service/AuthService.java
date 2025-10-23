package org.stockify.security.service;

import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.stockify.model.entity.UserEntity;
import org.stockify.model.repository.UserRepository;
import org.stockify.security.exception.AuthenticationException;
import org.stockify.security.model.dto.request.RegisterCredentialsRequest;
import org.stockify.security.model.dto.request.RegisterRequest;
import org.stockify.security.model.entity.CredentialsEntity;
import org.stockify.security.model.entity.RoleEntity;
import org.stockify.security.repository.CredentialRepository;
import org.stockify.security.model.dto.request.AuthRequest;
import org.stockify.security.repository.PermitRepository;
import org.stockify.security.repository.RolRepository;

import java.util.HashSet;
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

        private final UserRepository userRepository;

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
                       RolRepository rolRepository,
                       UserRepository userRepository) {
        this.credentialsRepository = credentialsRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.permitRepository = permitRepository;
        this.rolRepository = rolRepository;
        this.userRepository = userRepository;
    }

    // Removed legacy role+permit combined update methods from AuthService.

    // Removed permit helper methods; permits are managed via roles.

    /**
     * Registers a user profile together with credentials using a composite request.
     * Assigns default CLIENT role which must exist.
     */
    public CredentialsEntity register(RegisterRequest request) {
        if (request == null || request.getCredentials() == null || request.getUser() == null) {
            throw new AuthenticationException("Invalid register payload", null);
        }

        String email = request.getCredentials().getEmail();
        if (credentialsRepository.existsByEmail(email)) {
            throw new AuthenticationException("User already exists with email: " + email, null);
        }

        // Create user profile
        UserEntity user = UserEntity.builder()
                .firstName(request.getUser().getFirstName())
                .lastName(request.getUser().getLastName())
                .dni(request.getUser().getDni())
                .phone(request.getUser().getPhone())
                .img(request.getUser().getImg())
                .build();
        user = userRepository.save(user);

        // Create credentials linked to profile
        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(request.getCredentials().getUsername())
                .email(email)
                .password(passwordEncoder.encode(request.getCredentials().getPassword()))
                .user(user)
                .build();

        // Assign default CLIENT role (must exist)
        RoleEntity clientRole = rolRepository.findByName("CLIENT")
                .orElseThrow(() -> new AuthenticationException("Default role CLIENT not found. Please create it first.", null));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(clientRole);
        credentials.setRoles(roles);

        return credentialsRepository.save(credentials);
    }
    /**
     * Registers new credentials and optionally links them to an existing user profile.
     * Assigns default EMPLOYEE role with READ and WRITE permits.
     */
    public CredentialsEntity registerCredentials(RegisterCredentialsRequest request) {
        if (credentialsRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("User already exists with email: " + request.getEmail(), null);
        }

        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        // Assign default CLIENT role (must exist)
        RoleEntity clientRole = rolRepository.findByName("CLIENT")
                .orElseThrow(() -> new AuthenticationException("Default role CLIENT not found. Please create it first.", null));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(clientRole);
        credentials.setRoles(roles);

        // Optionally link to existing user profile
        if (request.getUserId() != null) {
            UserEntity user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new UsernameNotFoundException("User profile not found with id: " + request.getUserId()));
            credentials.setUser(user);
        }

        return credentialsRepository.save(credentials);
    }

    /**
     * Registers a new user profile along with credentials in a single transaction-like operation.
     */
    public CredentialsEntity registerUserWithCredentials(org.stockify.security.model.dto.request.RegisterUserCredentialsRequest request) {
        if (credentialsRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("User already exists with email: " + request.getEmail(), null);
        }

        // Create user profile first
        UserEntity user = UserEntity.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dni(request.getDni())
                // email is managed by credentials only
                .phone(request.getPhone())
                .img(request.getImg())
                .build();
        user = userRepository.save(user);

        // Create credentials linked to the user
        CredentialsEntity credentials = CredentialsEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .user(user)
                .build();

        // Assign default CLIENT role (must exist)
        RoleEntity clientRole = rolRepository.findByName("CLIENT")
                .orElseThrow(() -> new AuthenticationException("Default role CLIENT not found. Please create it first.", null));
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(clientRole);
        credentials.setRoles(roles);

        return credentialsRepository.save(credentials);
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
