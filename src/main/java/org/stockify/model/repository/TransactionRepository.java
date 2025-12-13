package org.stockify.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.stockify.model.entity.TransactionEntity;
import org.stockify.model.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    List<TransactionEntity> findAllByPaymentStatusAndDateTimeBefore(PaymentStatus status, LocalDateTime dateTime);
}
