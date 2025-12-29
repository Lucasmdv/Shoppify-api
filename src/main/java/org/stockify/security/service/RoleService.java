package org.stockify.security.service;

import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.stockify.security.model.dto.request.RoleCreateRequest;
import org.stockify.security.model.entity.CredentialsEntity;
import org.stockify.security.model.entity.PermitEntity;
import org.stockify.security.model.entity.RoleEntity;
import org.stockify.security.model.enums.Permit;
import org.stockify.security.repository.CredentialRepository;
import org.stockify.security.repository.PermitRepository;
import org.stockify.security.repository.RolRepository;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Transactional
@Service
public class RoleService {

    private final CredentialRepository credentialsRepository;
    private final PermitRepository permitRepository;
    private final RolRepository rolRepository;

    public RoleService(CredentialRepository credentialsRepository,
                       PermitRepository permitRepository,
                       RolRepository rolRepository) {
        this.credentialsRepository = credentialsRepository;
        this.permitRepository = permitRepository;
        this.rolRepository = rolRepository;
    }

    // Removed legacy methods that mixed role and permit updates in one call.

    public RoleEntity createRole(RoleCreateRequest request) {
        String normalized = request.getName().toUpperCase(Locale.ROOT);
        RoleEntity role = rolRepository.findByName(normalized).orElse(null);
        if (role == null) {
            role = RoleEntity.builder()
                    .name(normalized)
                    .description(request.getDescription())
                    .build();
        } else {
            role.setDescription(request.getDescription());
        }

        if (request.getPermits() != null) {
            Set<PermitEntity> permitEntities = new HashSet<>();
            for (Permit p : request.getPermits()) {
                PermitEntity permitEntity = permitRepository.findByPermit(p)
                        .orElseGet(() -> permitRepository.save(PermitEntity.builder().permit(p).build()));
                permitEntities.add(permitEntity);
            }
            role.setPermits(permitEntities);
        }
        return rolRepository.save(role);
    }

    public RoleEntity setRolePermitsById(Long roleId, Set<Permit> permits) {
        RoleEntity role = rolRepository.findById(roleId)
                .orElseThrow(() -> new UsernameNotFoundException("Role not found with id: " + roleId));
        Set<PermitEntity> permitEntities = new HashSet<>();
        for (Permit p : permits) {
            PermitEntity permitEntity = permitRepository.findByPermit(p)
                    .orElseGet(() -> permitRepository.save(PermitEntity.builder().code(p.name().toLowerCase(java.util.Locale.ROOT)).permit(p).build()));
            permitEntities.add(permitEntity);
        }
        role.setPermits(permitEntities);
        return rolRepository.save(role);
    }

    public CredentialsEntity assignRoleToUserById(String email, Long roleId) {
        RoleEntity role = rolRepository.findById(roleId)
                .orElseThrow(() -> new UsernameNotFoundException("Role not found with id: " + roleId));
        CredentialsEntity credentials = credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        Set<RoleEntity> roles = credentials.getRoles();
        if (roles == null) roles = new HashSet<>(); else roles = new HashSet<>(roles);
        roles.add(role);
        credentials.setRoles(roles);
        return credentialsRepository.save(credentials);
    }
}
