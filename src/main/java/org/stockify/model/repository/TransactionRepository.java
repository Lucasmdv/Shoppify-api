package org.stockify.model.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stockify.model.entity.TransactionEntity;
import org.stockify.model.enums.PaymentStatus;
import org.stockify.model.enums.TransactionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByPaymentStatusAndDateTimeBefore(PaymentStatus status, LocalDateTime dateTime);

    @EntityGraph(attributePaths = {
            "detailTransactions",
            "detailTransactions.product"
    })
    Optional<TransactionEntity> findFirstByIdempotencyKeyAndPaymentStatusAndType(
            String idempotencyKey,
            PaymentStatus paymentStatus,
            TransactionType type
    );
}
