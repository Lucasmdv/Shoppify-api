package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.stockify.dto.request.order.OrderFilterRequest;
import org.stockify.dto.request.order.OrderRequest;
import org.stockify.dto.request.order.UpdateOrderRequest;
import org.stockify.dto.request.sale.SaleFilterRequest;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.dto.response.OrderResponse;
import org.stockify.dto.response.SaleResponse;
import org.stockify.dto.response.TransactionResponse;
import org.stockify.model.entity.*;
import org.stockify.model.enums.OrderStatus;
import org.stockify.model.enums.TransactionType;
import org.stockify.model.exception.InsufficientStockException;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.OrderMapper;
import org.stockify.model.mapper.SaleMapper;
import org.stockify.model.repository.OrderRepository;
import org.stockify.model.repository.SaleRepository;
import org.stockify.model.specification.OrderSpecification;
import org.stockify.model.specification.SaleSpecification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new NotFoundException("Order with ID " + id + " not found");
        }
        orderRepository.deleteById(id);
    }

    public Page<OrderResponse> findAll(OrderFilterRequest filterRequest, Pageable pageable) {
        Specification<OrderEntity> specification = Specification
                .where(OrderSpecification.byClient(filterRequest.getClientId()))
                .and(OrderSpecification.byOrder(filterRequest.getOrderId()))
                .and(OrderSpecification.byStatus(mapStatus(filterRequest.getStatus())))
                .and(OrderSpecification.byEndDate(filterRequest.getEndDate()))
                .and(OrderSpecification.byStartDate(filterRequest.getStartDate()))
                .and(OrderSpecification.byTotalRange(filterRequest.getMinPrice(),  filterRequest.getMaxPrice()))
                .and(OrderSpecification.byPickup(filterRequest.getPickup()));

        Page<OrderEntity> orderEntities = orderRepository.findAll(specification, pageable);
        return orderEntities.map(orderMapper::toResponseDTO);
    }

    public OrderResponse findById(Long id) {
        OrderEntity orderEntity = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with ID " + id + " not found"));
        return orderMapper.toResponseDTO(orderEntity);
    }

    public List<OrderResponse> findOrdersByUser(Long userId) {
        List<OrderEntity> orders = orderRepository.findByClientId(userId);
        if (orders.isEmpty()) {
            throw new NotFoundException("Orders for client id " + userId + " not found");
        }
        return orders.stream()
                .map(orderMapper::toResponseDTO)
                .toList();
    }

    public OrderResponse updateOrderPartial(Long id, UpdateOrderRequest orderRequest) {
        OrderEntity existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with ID " + id + " not found"));
        orderMapper.partialUpdateOrderEntity(orderRequest, existingOrder);

        OrderEntity updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    public OrderResponse updateOrderFull(Long id, UpdateOrderRequest orderRequest) {
        OrderEntity existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order with ID " + id + " not found"));

        orderMapper.updateOrderEntity(orderRequest, existingOrder);

        OrderEntity updatedOrder = orderRepository.save(existingOrder);
        return orderMapper.toResponseDTO(updatedOrder);
    }

    private OrderStatus mapStatus(String status) {
        if (status == null || status.isBlank()) return null;
        return OrderStatus.valueOf(status);
    }
}