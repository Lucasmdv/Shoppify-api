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

import java.math.BigDecimal;
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

    public TransactionResponse saveTransaction(TransactionCreatedRequest request, TransactionType type) {
        TransactionEntity transactionEntity = transactionMapper.toEntity(request);
        transactionEntity.setTotal(request.getTotalAmount());
        transactionEntity.setDescription(request.getDescription());
        transactionEntity.setType(type);
        transactionEntity.setStore(resolveDefaultStore());
        return transactionMapper.toDto(transactionRepository.save(transactionEntity));
    }

    public TransactionEntity createTransaction(TransactionRequest request, TransactionType type) {
        Set<DetailTransactionEntity> detailTransactions = request
                .getDetailTransactions()
                .stream()
                .map(detailRequest -> {
                    ProductEntity product = productRepository.findById(detailRequest.getProductID())
                            .orElseThrow(() -> new NotFoundException("Product with ID " + detailRequest.getProductID() + " not found."));

                    DetailTransactionEntity entity = new DetailTransactionEntity();
                    entity.setProduct(product);

                    BigDecimal quantity = BigDecimal.valueOf(detailRequest.getQuantity());
                    entity.setQuantity(quantity);

                    BigDecimal unitPrice = resolveUnitPrice(product, type);
                    entity.setSubtotal(unitPrice.multiply(quantity));

                    return entity;
                })
                .collect(Collectors.toSet());

        TransactionEntity transactionEntity = transactionMapper.toEntity(request);
        transactionEntity.setStore(resolveDefaultStore());
        transactionEntity.setDetailTransactions(detailTransactions);
        detailTransactions.forEach(detail -> detail.setTransaction(transactionEntity));

        transactionEntity.setTotal(
                detailTransactions.stream()
                        .map(DetailTransactionEntity::getSubtotal)
                        .reduce(BigDecimal::add)
                        .orElse(BigDecimal.ZERO)
        );

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
        if (type == TransactionType.PURCHASE && product.getUnitPrice() != null) {
            return product.getUnitPrice();
        }
        if (product.getPrice() != null) {
            return product.getPrice();
        }
        return BigDecimal.ZERO;
    }


    private StoreEntity resolveDefaultStore() {
        return storeRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new NotFoundException("Store configuration not found. Please register a store before creating transactions."));
    }
}


