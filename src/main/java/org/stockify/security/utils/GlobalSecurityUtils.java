package org.stockify.security.utils;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.stockify.model.repository.SaleRepository;
import org.stockify.security.model.entity.CredentialsEntity;

@Component("securityRules")
public class GlobalSecurityUtils {

    private final SaleRepository saleRepository;

    public GlobalSecurityUtils(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    /**
     * Checks whether the authenticated user is the owner of the sale.
     *
     * @param authentication the current authentication object
     * @param saleId         the sale identifier
     * @return true if the sale exists and belongs to the authenticated user
     */
    public boolean isSaleOwner(Authentication authentication, Long saleId) {
        if (authentication == null || !(authentication.getPrincipal() instanceof CredentialsEntity)) {
            return false;
        }

        CredentialsEntity currentUser = (CredentialsEntity) authentication.getPrincipal();

        return saleRepository.findById(saleId)
                .map(sale -> sale.getUser() != null
                        && sale.getUser().getId().equals(currentUser.getUser().getId()))
                .orElse(false);
    }
}
