package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.dto.request.transaction.TransactionCreatedRequest;
import org.stockify.dto.request.transaction.TransactionRequest;
import org.stockify.dto.response.TransactionResponse;
import org.stockify.model.entity.*;
import org.stockify.model.enums.OrderStatus;
import org.stockify.model.enums.PaymentStatus;
import org.stockify.model.enums.TransactionType;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.TransactionMapper;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.ShipmentRepository;
import org.stockify.model.repository.StoreRepository;
import org.stockify.model.repository.TransactionRepository;
import org.stockify.util.PriceCalculator;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PriceCalculator priceCalculator;
    private final ShipmentRepository shipmentRepository;

    public TransactionResponse saveTransaction(TransactionCreatedRequest request, TransactionType type) {
        TransactionEntity transactionEntity = transactionMapper.toEntity(request);
        transactionEntity.setTotal(request.getTotalAmount());
        transactionEntity.setDescription(request.getDescription());
        transactionEntity.setType(type);
        transactionEntity.setStore(resolveDefaultStore());
        return transactionMapper.toDto(transactionRepository.save(transactionEntity));
    }

    public TransactionEntity createTransaction(TransactionRequest request, TransactionType type) {
        return createTransaction(request, type, null);
    }

    public TransactionEntity createTransaction(TransactionRequest request, TransactionType type, String idempotencyKey) {

        TransactionEntity transactionEntity = transactionMapper.toEntity(request);
        transactionEntity.setStore(resolveDefaultStore());

        Set<DetailTransactionEntity> detailTransactions = new HashSet<>();
        BigDecimal total = BigDecimal.ZERO;
        for (DetailTransactionRequest detailRequest : request.getDetailTransactions()) {

            ProductEntity product = resolveProduct(detailRequest.getProductID());

            DetailTransactionEntity entity = new DetailTransactionEntity();
            entity.setProduct(product);
            entity.setQuantity(detailRequest.getQuantity());
            entity.setTransaction(transactionEntity);

            BigDecimal unitPrice = resolveUnitPrice(product, type);
            BigDecimal subtotal = priceCalculator.calculateSubtotal(unitPrice, detailRequest.getQuantity());
            entity.setSubtotal(subtotal);
            detailTransactions.add(entity);
            total = total.add(subtotal);
        }

        transactionEntity.setDetailTransactions(detailTransactions);
        transactionEntity.setTotal(total);
        transactionEntity.setDescription(request.getDescription());
        transactionEntity.setType(type);
        transactionEntity.setPaymentStatus(PaymentStatus.PENDING);
        transactionEntity.setIdempotencyKey(idempotencyKey);

        return transactionRepository.save(transactionEntity);
    }

    public List<TransactionResponse> findAll() {
        return transactionRepository.findAll()
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }

    private BigDecimal resolveUnitPrice(ProductEntity product, TransactionType type) {

        if (type == TransactionType.PURCHASE) {
            return product.getUnitPrice() != null ? product.getUnitPrice() : BigDecimal.ZERO;
        }
        if (type == TransactionType.SALE) {
            return priceCalculator.calculateDiscountPrice(product);
        }
        return product.getPrice() != null ? product.getPrice() : BigDecimal.ZERO;
    }

    public void cancelExpiredTransactions(int timeoutMinutes) {
        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(timeoutMinutes);
        List<TransactionEntity> expiredTransactions = transactionRepository.findAllByPaymentStatusAndDateTimeBefore(
                PaymentStatus.PENDING, expirationTime);

        if (expiredTransactions.isEmpty()) return;

        log.info("Found {} expired pending transactions to cancel (Timeout: {} min)", expiredTransactions.size(),
                timeoutMinutes);

        for (TransactionEntity transaction : expiredTransactions) {
            try {
                cancelTransaction(transaction, "EXPIRED");
                ShipmentEntity shipment = transaction.getSale().getShipment();
                shipment.setStatus(OrderStatus.CANCELLED);
                shipmentRepository.save(shipment);
            } catch (Exception e) {
                log.error("Error cancelling expired transaction ID: {}", transaction.getId(), e);
            }
        }
    }

    public void cancelTransactionById(Long id, String reason) {
        TransactionEntity transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found with id: " + id));
        cancelTransaction(transaction, reason);
    }

    public void cancelTransaction(TransactionEntity transaction, String reason) {
        if (transaction.getPaymentStatus() != PaymentStatus.PENDING) {
            log.warn("Attempted to cancel transaction {} but status is {}", transaction.getId(), transaction.getPaymentStatus());
            return;
        }

        transaction.setPaymentStatus(PaymentStatus.CANCELLED); // Or REJECTED (?
        transaction.setPaymentLink(null);
        transactionRepository.save(transaction);

        restoreStock(transaction);
        log.info("Transaction {} cancelled. Reason: {}. Stock restored.", transaction.getId(), reason);
    }

    public void restoreStock(TransactionEntity transaction) {
        if (transaction.getDetailTransactions() == null) return;

        for (DetailTransactionEntity detail : transaction.getDetailTransactions()) {
            ProductEntity product = detail.getProduct();
            if (product != null) {
                long quantityToRestore = detail.getQuantity();
                long currentStock = product.getStock() == null ? 0 : product.getStock();
                long currentSold = product.getSoldQuantity() == null ? 0 : product.getSoldQuantity();

                product.setStock(currentStock + quantityToRestore);
                product.setSoldQuantity(Math.max(0, currentSold - quantityToRestore));

                productRepository.save(product);
                log.info("Restored {} units for product {}. New Stock: {}", quantityToRestore, product.getName(), product.getStock());
            }
        }
    }

    private StoreEntity resolveDefaultStore() {
        return storeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Store configuration not found. Please register a store before creating transactions."));
    }

    private ProductEntity resolveProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
    }
}
