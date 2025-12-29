package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.stockify.dto.request.product.ProductRequest;
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
import org.stockify.model.repository.StoreRepository;
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
    private final StoreRepository storeRepository;

    public ShipmentEntity mapShipment(ShipmentRequest request, SaleEntity sale) {
        ShipmentEntity shipment = shipmentMapper.toEntity(request, sale);

        if (Boolean.TRUE.equals(request.getPickup())) {
            StoreEntity store = storeRepository.findById(1L)
                    .orElseThrow(() -> new NotFoundException("Store not found"));
            shipment.setStreet(store.getAddress());
            shipment.setNumber(null);
            shipment.setCity(store.getCity());
            shipment.setZip(parsePostalCode(store.getPostalCode()));
        }

        long quantity = sale.getTransaction() == null ? 0
                : sale.getTransaction().getDetailTransactions().stream()
                .mapToLong(org.stockify.model.entity.DetailTransactionEntity::getQuantity)
                .sum();

        Double shippingCost = calculateShippingCost(quantity);

        if (shippingCost != null) {
            shipment.setShipmentCost(shippingCost);
        }

        return shipment;
    }

    private Integer parsePostalCode(String postalCode) {
        if (postalCode == null || postalCode.isBlank()) {
            return null;
        }
        String digits = postalCode.replaceAll("\\D+", "");
        if (digits.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Double calculateShippingCost(long quantity) {
        StoreEntity store = storeRepository.findById(1L)
                .orElseThrow(() -> new NotFoundException("Store not found"));

        if (quantity <= 4) {
            return store.getShippingCostSmall().doubleValue();
        } else if (quantity <= 6) {
            return store.getShippingCostMedium().doubleValue();
        } else {
            return store.getShippingCostLarge().doubleValue();
        }
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
                .and(ShipmentSpecification.byCity(filterRequest.getCity()));

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
        OrderStatus oldStatus = existingShipment.getStatus();
        shipmentMapper.partialUpdateOrderEntity(request, existingShipment);

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
            Long saleId = savedEntity.getSale() != null ? savedEntity.getSale().getId() : null;
            Long userId = savedEntity.getSale() != null && savedEntity.getSale().getUser() != null
                    ? savedEntity.getSale().getUser().getId()
                    : null;
            eventPublisher.publishEvent(new ShipmentStateUpdatedEvent(
                    savedEntity.getId(),
                    oldStatus,
                    savedEntity.getStatus(),
                    saleId,
                    userId
            ));
        }
    }
}
