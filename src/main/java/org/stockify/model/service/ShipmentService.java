package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.stockify.dto.request.product.ProductRequest;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.request.shipment.ShipmentFilterRequest;
import org.stockify.dto.request.shipment.ShipmentRequest;
import org.stockify.dto.request.shipment.UpdateShipmentRequest;
import org.stockify.dto.response.ShipmentResponse;
import org.stockify.model.entity.*;
import org.stockify.model.enums.OrderStatus;
import org.stockify.model.event.ProductStockUpdatedEvent;
import org.stockify.model.event.ShipmentStateUpdatedEvent;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.ShipmentMapper;
import org.stockify.model.repository.ShipmentRepository;
import org.stockify.model.specification.ShipmentSpecification;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ShipmentService {
    private final ShipmentRepository shipmentRepository;
    private final ShipmentMapper shipmentMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void createShipmentIfNeeded(TransactionEntity transaction) {
        SaleEntity sale = transaction.getSale();
        if (sale == null || sale.getShipment() != null) {
            return;
        }

        ShipmentEntity shipment = new ShipmentEntity();
        shipment.setSale(sale);
        shipment.setStatus(OrderStatus.PROCESSING);
        shipment.setStartDate(LocalDate.now());
        shipment.setPickup(transaction.getSale().getShippingInfo().getPickup());
        shipment.setAdress(transaction.getSale().getShippingInfo().getAdress());
        sale.setShipment(shipment);
    }

    public void delete(Long id) {
        if (!shipmentRepository.existsById(id)) {
            throw new NotFoundException("Order with ID " + id + " not found");
        }
        shipmentRepository.deleteById(id);
    }

    public Page<ShipmentResponse> findAll(ShipmentFilterRequest filterRequest, Pageable pageable) {
        Specification<ShipmentEntity> specification = Specification
                .where(ShipmentSpecification.byClient(filterRequest.getClientId()))
                .and(ShipmentSpecification.byOrder(filterRequest.getOrderId()))
                .and(ShipmentSpecification.byStatus(mapStatus(filterRequest.getStatus())))
                .and(ShipmentSpecification.byEndDate(filterRequest.getEndDate()))
                .and(ShipmentSpecification.byStartDate(filterRequest.getStartDate()))
                .and(ShipmentSpecification.byTotalRange(filterRequest.getMinPrice(),  filterRequest.getMaxPrice()))
                .and(ShipmentSpecification.byPickup(filterRequest.getPickup()))
                .and(ShipmentSpecification.byAdress(filterRequest.getAdress()));

        Page<ShipmentEntity> orderEntities = shipmentRepository.findAll(specification, pageable);
        return orderEntities.map(shipmentMapper::toResponseDTO);
    }

    public ShipmentResponse findById(Long id) {
        ShipmentEntity shipmentEntity = shipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with ID " + id + " not found"));
        return shipmentMapper.toResponseDTO(shipmentEntity);
    }

    public List<ShipmentResponse> findOrdersByUser(Long userId) {
        List<ShipmentEntity> orders = shipmentRepository.findByClientId(userId);
        if (orders.isEmpty()) {
            throw new NotFoundException("Orders for client id " + userId + " not found");
        }
        return orders.stream()
                .map(shipmentMapper::toResponseDTO)
                .toList();
    }

    public ShipmentResponse updateOrderPartial(Long id, UpdateShipmentRequest request) {
        ShipmentEntity existingShipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with ID " + id + " not found"));
        shipmentMapper.partialUpdateOrderEntity(request, existingShipment);
        OrderStatus oldStatus = existingShipment.getStatus();

        switch(request.getStatus()) {
            case "DELIVERED", "CANCELLED", "RETURNED"
                    -> existingShipment.setEndDate(LocalDate.now());
        }

        ShipmentEntity updatedShipment = shipmentRepository.save(existingShipment);
        statusTrigger(request, updatedShipment, oldStatus);

        return shipmentMapper.toResponseDTO(updatedShipment);
    }

    private OrderStatus mapStatus(String status) {
        if (status == null || status.isBlank()) return null;
        return OrderStatus.valueOf(status);
    }

    private void statusTrigger(UpdateShipmentRequest request, ShipmentEntity savedEntity, OrderStatus oldStatus){
        if (request.getStatus() == null) return;
        if (savedEntity.getStatus() != oldStatus) {
            eventPublisher.publishEvent(new ShipmentStateUpdatedEvent(
                    savedEntity.getId(),
                    oldStatus,
                    savedEntity.getStatus()
            ));
        }
    }
}