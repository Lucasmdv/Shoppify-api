package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.dto.request.transaction.TransactionCreatedRequest;
import org.stockify.dto.request.transaction.TransactionRequest;
import org.stockify.dto.response.TransactionResponse;
import org.stockify.model.entity.DetailTransactionEntity;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.StoreEntity;
import org.stockify.model.entity.TransactionEntity;
import org.stockify.model.enums.TransactionType;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.TransactionMapper;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.StoreRepository;
import org.stockify.model.repository.TransactionRepository;
import org.stockify.util.PriceCalculator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final PriceCalculator priceCalculator;

    public TransactionResponse saveTransaction(TransactionCreatedRequest request, TransactionType type) {
        TransactionEntity transactionEntity = transactionMapper.toEntity(request);
        transactionEntity.setTotal(request.getTotalAmount());
        transactionEntity.setDescription(request.getDescription());
        transactionEntity.setType(type);
        transactionEntity.setStore(resolveDefaultStore());
        return transactionMapper.toDto(transactionRepository.save(transactionEntity));
    }

    public TransactionEntity createTransaction(TransactionRequest request, TransactionType type) {

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

    private StoreEntity resolveDefaultStore() {
        return storeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Store configuration not found. Please register a store before creating transactions."));
    }

    private ProductEntity resolveProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
    }
}


