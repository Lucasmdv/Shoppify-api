package org.stockify.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stockify.model.entity.CartEntity;
import org.stockify.model.entity.OrderEntity;
import org.stockify.model.entity.SaleEntity;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity,Long>, JpaSpecificationExecutor<OrderEntity> {
    @Query("SELECT o FROM OrderEntity o WHERE o.sale.user.id = :clientId")
    List<OrderEntity> findByClientId(@Param("clientId") Long clientId);
}