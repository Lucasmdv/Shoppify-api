package org.stockify.model.specification;

import org.springframework.data.jpa.domain.Specification;
import org.stockify.model.entity.SaleEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SaleSpecification {

    public static Specification<SaleEntity> byTransactionId(Long transactionId) {
        return (root, query, cb) ->
                transactionId == null ? null : cb.equal(root.get("transaction").get("id"), transactionId);
    }

    public static Specification<SaleEntity> byClientId(Long clientId) {
        return (root, query, cb) ->
                clientId == null ? null : cb.equal(root.get("client").get("id"), clientId);
    }

    public static Specification<SaleEntity> bySaleId(Long saleId) {
        return (root, query, cb) ->
                saleId == null ? null : cb.equal(root.get("id"), saleId);
    }

    public static Specification<SaleEntity> byStartDate(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) return null;
            LocalDateTime startOfDay = startDate.atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("dateTime"), startOfDay);
        };
    }

    public static Specification<SaleEntity> byEndDate(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) return null;
            LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
            return cb.lessThanOrEqualTo(root.get("dateTime"), endOfDay);
        };
    }

    public static Specification<SaleEntity> byPaymentMethod(String method) {
        return (root, query, cb) ->
                (method == null || method.isEmpty()) ? null : cb.equal(root.get("paymentMethod"), method);
    }

    public static Specification<SaleEntity> byTotalRange(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) {
                return cb.between(root.get("total"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("total"), min);
            } else {
                return cb.lessThanOrEqualTo(root.get("total"), max);
            }
        };
    }
}
