package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.stockify.dto.request.purchase.PurchaseFilterRequest;
import org.stockify.dto.request.purchase.PurchaseRequest;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.dto.response.PurchaseResponse;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.ProviderEntity;
import org.stockify.model.entity.PurchaseEntity;
import org.stockify.model.entity.TransactionEntity;
import org.stockify.model.enums.TransactionType;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.PurchaseMapper;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.ProviderRepository;
import org.stockify.model.repository.PurchaseRepository;
import org.stockify.model.specification.PurchaseSpecification;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final PurchaseMapper purchaseMapper;
    private final TransactionService transactionService;
    private final ProviderRepository providerRepository;
    private final ProductRepository productRepository;

    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request) {
        if (request.getTransaction() == null || request.getTransaction().getDetailTransactions() == null
                || request.getTransaction().getDetailTransactions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Purchase requires at least one product detail");
        }

        ProviderEntity provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new NotFoundException("Provider not found with ID " + request.getProviderId()));

        List<ProductEntity> productsToUpdate = new ArrayList<>();
        for (DetailTransactionRequest detail : request.getTransaction().getDetailTransactions()) {
            ProductEntity product = productRepository.findById(detail.getProductID())
                    .orElseThrow(() -> new NotFoundException("Product not found with ID " + detail.getProductID()));

            long quantity = detail.getQuantity();
            long currentStock = product.getStock() == null ? 0L : product.getStock();
            product.setStock(currentStock + quantity);
            productsToUpdate.add(product);
        }

        TransactionEntity transaction = transactionService.createTransaction(
                request.getTransaction(), TransactionType.PURCHASE
        );

        PurchaseEntity purchase = purchaseMapper.toEntity(request);
        purchase.setTransaction(transaction);
        purchase.setProvider(provider);
        purchase.setUnitPrice(request.getUnitPrice());

        productRepository.saveAll(productsToUpdate);
        PurchaseEntity saved = purchaseRepository.save(purchase);
        return purchaseMapper.toResponseDTO(saved);
    }

    public PurchaseResponse updatePurchase(Long id, PurchaseRequest request) {
        PurchaseEntity purchaseEntity = purchaseMapper.toEntity(request);
        purchaseEntity.setId(id);
        return purchaseMapper.toResponseDTO(purchaseRepository.save(purchaseEntity));
    }

    public void deletePurchase(Long id) {
        purchaseRepository.deleteById(id);
    }

    public Page<PurchaseResponse> getAllPurchases(Pageable pageable, PurchaseFilterRequest request) {
        Specification<PurchaseEntity> spec = Specification
                .where(PurchaseSpecification.ByTransactionId(request.getTransactionId()))
                .and(PurchaseSpecification.ByProviderId(request.getProviderId()))
                .and(PurchaseSpecification.ByPurchaseId(request.getPurchaseId()));
        return purchaseRepository.findAll(spec, pageable)
                .map(purchaseMapper::toResponseDTO);
    }

    public PurchaseResponse findById(Long id) {
        PurchaseEntity purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Purchase not found with id: " + id));
        return purchaseMapper.toResponseDTO(purchase);
    }
}

