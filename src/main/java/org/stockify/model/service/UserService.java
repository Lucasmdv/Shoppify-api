package org.stockify.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.stockify.dto.request.user.UserFilterRequest;
import org.stockify.dto.request.user.UserRequest;
import org.stockify.dto.response.UserResponse;
import org.stockify.model.entity.UserEntity;
import org.stockify.model.exception.ClientNotFoundException;
import org.stockify.model.mapper.UserMapper;
import org.stockify.model.repository.UserRepository;
import org.stockify.model.specification.UserSpecification;
import org.stockify.security.exception.AuthenticationException;
import org.stockify.security.model.dto.request.AuthRequest;
import org.stockify.security.model.entity.CredentialsEntity;
import org.stockify.security.model.entity.PermitEntity;
import org.stockify.security.model.entity.RoleEntity;
import org.stockify.security.model.enums.Permit;
import org.stockify.security.model.enums.Role;
import org.stockify.security.repository.CredentialRepository;
import org.stockify.security.repository.PermitRepository;
import org.stockify.security.repository.RolRepository;
import org.stockify.security.service.JwtService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class responsible for managing client-related operations,
 * including searching, saving, updating, and deleting clients.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CredentialRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolRepository rolRepository;
    private final PermitRepository permitRepository;
    private final JwtService jwtService;

    /**
     * Finds a client by their ID.
     *
     * @param id the ID of the client to find
     * @return a DTO containing the client data
     * @throws ClientNotFoundException if no client is found with the specified ID
     */
    public UserResponse findById(Long id) {
        UserEntity clientEntity = userRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client with id " + id + " not found"));
        return userMapper.toDto(clientEntity);
    }

    /**
     * Searches for clients matching the given filter criteria with pagination.
     *
     * @param filterRequest DTO containing filter criteria (first name, last name, DNI, phone)
     * @param pageable      pagination information
     * @return a paginated list of clients matching the filters
     */
    public Page<UserResponse> findAll(UserFilterRequest filterRequest, Pageable pageable) {
        Specification<UserEntity> specification = Specification
                .where(UserSpecification.firstNameLike(filterRequest.getFirstName()))
                .and(UserSpecification.lastNameLike(filterRequest.getLastName()))
                .and(UserSpecification.dniEquals(filterRequest.getDni()))
                .and(UserSpecification.phoneLike(filterRequest.getPhone()));

        Page<UserEntity> clients = userRepository.findAll(specification, pageable);
        return clients.map(userMapper::toDto);
    }

    /**
     * Saves a new client in the system.
     *
     * @param clientRequest DTO containing the client data to create
     * @return a DTO with the saved client data
     */
    public UserResponse save(UserRequest clientRequest) {
        UserEntity clientEntity = userMapper.toEntity(clientRequest);
        return userMapper.toDto(userRepository.save(clientEntity));
    }

    /**
     * Deletes a client by their ID.
     *
     * @param id the ID of the client to delete
     * @throws ClientNotFoundException if no client is found with the specified ID
     */
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ClientNotFoundException("Client with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    /**
     * Partially updates an existing client with the provided data.
     *
     * @param id            the ID of the client to update partially
     * @param clientRequest DTO containing the fields to update
     * @return a DTO with the updated client data
     * @throws ClientNotFoundException if no client is found with the specified ID
     */
    public UserResponse updateClientPartial(Long id, UserRequest clientRequest) {
        UserEntity existingClient = userRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client with id " + id + " not found"));

        userMapper.partialUpdateClientEntity(clientRequest, existingClient);

        UserEntity updatedClient = userRepository.save(existingClient);
        return userMapper.toDto(updatedClient);
    }

    /**
     * Fully updates an existing client with the provided data.
     *
     * @param id            the ID of the client to update
     * @param clientRequest DTO containing the new client data
     * @return a DTO with the updated client data
     * @throws ClientNotFoundException if no client is found with the specified ID
     */
    public UserResponse updateClientFull(Long id, UserRequest clientRequest) {
        UserEntity existingClient = userRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("Client with id " + id + " not found"));

        userMapper.updateClientEntity(clientRequest, existingClient);

        UserEntity updatedClient = userRepository.save(existingClient);
        return userMapper.toDto(updatedClient);
    }

    /**
     * Creates or retrieves the standard set of permissions for regular users.
     * This includes READ and WRITE permissions.
     *
     * @return List of permission entities for regular users
     */
    public List<PermitEntity> permitUser() {
        List<PermitEntity> permits = new ArrayList<>();

        // READ permiso
        PermitEntity readPermit = permitRepository.findByPermit(Permit.READ)
                .orElseGet(() -> {
                    PermitEntity newPermit = PermitEntity.builder().permit(Permit.READ).build();
                    return permitRepository.save(newPermit);
                });
        permits.add(readPermit);

        // WRITE permiso
        PermitEntity writePermit = permitRepository.findByPermit(Permit.WRITE)
                .orElseGet(() -> {
                    PermitEntity newPermit = PermitEntity.builder().permit(Permit.WRITE).build();
                    return permitRepository.save(newPermit);
                });
        permits.add(writePermit);

        return permits;
    }

    /**
     * Creates or retrieves the set of permissions for admin users.
     * This includes all available permissions.
     *
     * @return List of permission entities for admin users
     */
    public List<PermitEntity> permitEdit() {
        List<PermitEntity> permits = new ArrayList<>();

        // agrega todos los permisos para el rol ADMIN
        for (Permit permit : Permit.values()) {
            PermitEntity permitEntity = permitRepository.findByPermit(permit)
                    .orElseGet(() -> {
                        PermitEntity newPermit = PermitEntity.builder().permit(permit).build();
                        return permitRepository.save(newPermit);
                    });
            permits.add(permitEntity);
        }

        return permits;
    }

    /**
     * Generates a JWT token for the specified user.
     *
     * @param userDetails The user details for whom to generate the token
     * @return A JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return jwtService.generateToken(userDetails);
    }

    /**
     * Authenticates a user based on the provided authentication request.
     *
     * @param authRequest The authentication request containing user credentials
     * @return The authenticated user details
     * @throws AuthenticationException if authentication fails
     */
    public UserDetails createUser(AuthRequest authRequest) {
        if (credentialsRepository.findByEmail(authRequest.email()).isPresent()) {
            throw new AuthenticationException("User already exists with email: " + authRequest.email(), null);
        }

        CredentialsEntity newUser = CredentialsEntity.builder()
                .email(authRequest.email())
                .password(passwordEncoder.encode(authRequest.password()))
                .build();

        // Asigna rol USER por defecto
        Set<PermitEntity> userPermits = new HashSet<>(permitUser());
        RoleEntity employeeRole = rolRepository.findByRole(Role.EMPLOYEE)
                .orElseGet(() -> rolRepository.save(
                        RoleEntity.builder()
                                .role(Role.EMPLOYEE)
                                .permits(userPermits)
                                .build()
                ));

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(employeeRole);
        newUser.setRoles(roles);

        return credentialsRepository.save(newUser);
    }


    public UserDetails editPermitsUser(AuthRequest authRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationException("User is not authenticated", null);
        }

        UserDetails currentUser = (UserDetails) authentication.getPrincipal();
        if (!currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AuthenticationException("User does not have permission to edit permits", null);
        }

        CredentialsEntity user = credentialsRepository.findByEmail(authRequest.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + authRequest.email()));

        Set<PermitEntity> newPermits = new HashSet<>(permitEdit());
        RoleEntity adminRole = rolRepository.findByRole(Role.ADMIN)
                .orElseGet(() -> rolRepository.save(
                        RoleEntity.builder()
                                .role(Role.ADMIN)
                                .permits(newPermits)
                                .build()
                ));

        Set<RoleEntity> roles = user.getRoles();
        roles.add(adminRole);
        user.setRoles(roles);

        return credentialsRepository.save(user);
    }
}
