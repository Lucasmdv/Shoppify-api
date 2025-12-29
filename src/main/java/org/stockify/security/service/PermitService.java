package org.stockify.security.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.stockify.security.model.entity.PermitEntity;
import org.stockify.security.model.enums.Permit;
import org.stockify.security.repository.PermitRepository;

import java.util.List;

@Transactional
@Service
public class PermitService {

    private final PermitRepository permitRepository;

    public PermitService(PermitRepository permitRepository) {
        this.permitRepository = permitRepository;
    }

    public List<PermitEntity> listAll() {
        return permitRepository.findAll();
    }

    public PermitEntity create(Permit permit) {
        return permitRepository.findByPermit(permit)
                .orElseGet(() -> permitRepository.save(PermitEntity.builder()
                        .code(permit.name().toLowerCase(java.util.Locale.ROOT))
                        .permit(permit)
                        .build()));
    }
}
