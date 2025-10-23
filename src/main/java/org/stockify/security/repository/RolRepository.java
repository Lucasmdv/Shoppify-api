package org.stockify.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stockify.security.model.entity.RoleEntity;

import java.util.Optional;

@Repository
public interface RolRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(String name);
}
