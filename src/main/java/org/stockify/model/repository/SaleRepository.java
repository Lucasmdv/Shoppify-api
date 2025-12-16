package org.stockify.model.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.stockify.model.entity.SaleEntity;
import org.stockify.model.enums.PaymentStatus;
import org.stockify.model.enums.TransactionType;

import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity,Long>, JpaSpecificationExecutor<SaleEntity> {

    @EntityGraph(attributePaths = {
            "transaction",
            "transaction.detailTransactions",
            "transaction.detailTransactions.product"
    })
    Optional<SaleEntity> findFirstByUser_IdAndTransaction_PaymentStatusAndTransaction_TypeOrderByTransaction_DateTimeDesc(
            Long userId,
            PaymentStatus paymentStatus,
            TransactionType type
    );
}
