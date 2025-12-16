package org.stockify.model.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.stockify.model.entity.SaleEntity;
import org.stockify.model.enums.PaymentStatus;
import org.stockify.model.enums.TransactionType;

import org.springframework.data.jpa.domain.Specification;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface SaleRepository extends JpaRepository<SaleEntity, Long>, JpaSpecificationExecutor<SaleEntity> {

        @EntityGraph(attributePaths = {
                        "transaction",
                        "transaction.paymentDetail",
                        "transaction.detailTransactions",
                        "transaction.detailTransactions.product"
        })
        Optional<SaleEntity> findFirstByUser_IdAndTransaction_PaymentStatusAndTransaction_TypeOrderByTransaction_DateTimeDesc(
                        Long userId,
                        PaymentStatus paymentStatus,
                        TransactionType type);

        @Override
        @EntityGraph(attributePaths = {
                        "transaction",
                        "transaction.paymentDetail",
                        "transaction.detailTransactions",
                        "transaction.detailTransactions.product",
                        "user"
        })
        Page<SaleEntity> findAll(Pageable pageable);

        @Override
        @EntityGraph(attributePaths = {
                        "transaction",
                        "transaction.paymentDetail",
                        "transaction.detailTransactions",
                        "transaction.detailTransactions.product",
                        "user"
        })
        Optional<SaleEntity> findById(Long aLong);

        @EntityGraph(attributePaths = {
                        "transaction",
                        "transaction.paymentDetail",
                        "transaction.detailTransactions",
                        "transaction.detailTransactions.product",
                        "user"
        })
        Page<SaleEntity> findAll(Specification<SaleEntity> spec, Pageable pageable);

        @EntityGraph(attributePaths = {
                        "transaction",
                        "transaction.paymentDetail",
                        "transaction.detailTransactions",
                        "transaction.detailTransactions.product",
                        "user"
        })
        Optional<SaleEntity> findByIdAndUserId(Long id, Long userId);
}
