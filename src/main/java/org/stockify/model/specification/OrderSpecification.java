package org.stockify.model.specification;

import org.springframework.data.jpa.domain.Specification;
import org.stockify.model.entity.OrderEntity;
import org.stockify.model.enums.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class OrderSpecification {
    public static Specification<OrderEntity> byOrder(Long id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("id"), id);
    }

    public static Specification<OrderEntity> byClient(Long id) {
        return ((root, query, cb) ->
            id == null ? null : cb.equal(root.get("sale").get("client").get("id"), id));
    }

    public static Specification<OrderEntity> byStatus(OrderStatus status) {
        return (((root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status)));
    }

    public static Specification<OrderEntity> byStartDate(LocalDate startDate) {
        return (root, query, cb) -> {
            if (startDate == null) return null;
            LocalDateTime startOfDay = startDate.atStartOfDay();
            return cb.greaterThanOrEqualTo(root.get("startDate"), startOfDay);
        };
    }

    public static Specification<OrderEntity> byEndDate(LocalDate endDate) {
        return (root, query, cb) -> {
            if (endDate == null) return null;
            LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
            return cb.lessThanOrEqualTo(root.get("endDate"), endOfDay);
        };
    }

    public static Specification<OrderEntity> byTotalRange(Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            if (min != null && max != null) {
                return cb.between(root.get("sale").get("transaction").get("total"), min, max);
            } else if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("sale").get("transaction").get("total"), min);
            } else {
                return cb.lessThanOrEqualTo(root.get("sale").get("transaction").get("total"), max);
            }
        };
    }
}