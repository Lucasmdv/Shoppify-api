package org.stockify.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stockify.model.entity.ShipmentEntity;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<ShipmentEntity,Long>, JpaSpecificationExecutor<ShipmentEntity> {
    @Query("SELECT s FROM ShipmentEntity s WHERE s.sale.user.id = :clientId")
    List<ShipmentEntity> findByClientId(@Param("clientId") Long clientId);
}