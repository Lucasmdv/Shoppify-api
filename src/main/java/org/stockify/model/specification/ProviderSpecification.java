package org.stockify.model.specification;

import org.springframework.data.jpa.domain.Specification;
import org.stockify.model.entity.ProviderEntity;


public class ProviderSpecification {

    public static Specification<ProviderEntity> byName(String name) {
        return (root,query, cb) ->
                name== null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<ProviderEntity> byBusinessName(String businessName) {
        return (root, query, cb) ->
                businessName == null ? null : cb.like(cb.lower(root.get("businessName")), "%" + businessName.toLowerCase() + "%");
    }

    public static Specification<ProviderEntity> byTaxId(String taxId) {
        return (root, query, cb) ->
                taxId == null ? null : cb.like(cb.lower(root.get("taxId")), "%" + taxId.toLowerCase() + "%");
    }

    public static Specification<ProviderEntity> byEmail(String email) {
        return (root, query, cb) ->
                email == null ? null : cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<ProviderEntity> byPhone(String phone) {
        return (root, query, cb) ->
                phone == null ? null : cb.like(cb.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
    }

    public static Specification<ProviderEntity> byActive(String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.equal(root.get("active"), true);  //default: show active providers
            } else if (status.equalsIgnoreCase("all")) {
                return criteriaBuilder.isNotNull(root.get("active")); // show all providers regardless of active status if "all" is specified
            } else {
                Boolean activeStatus = Boolean.parseBoolean(status);  // Convert string "true" to boolean, any other value will be false
                return criteriaBuilder.equal(root.get("active"), activeStatus);
            }
        };
    }

    public static Specification<ProviderEntity> byId(Long id) {
        return (root, query, criteriaBuilder) ->
                id == null ? null : criteriaBuilder.equal(root.get("id"), id);
    }

    public static Specification<ProviderEntity> byTaxAddress(String taxAddress) {
        return (root, query, cb) ->
                taxAddress == null ? null : cb.like(cb.lower(root.get("taxAddress")), "%" + taxAddress.toLowerCase() + "%");
    }

}
