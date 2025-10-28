package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.stockify.dto.request.sale.SaleFilterRequest;
import org.stockify.dto.request.sale.SaleRequest;
import org.stockify.dto.request.transaction.DetailTransactionRequest;
import org.stockify.dto.response.SaleResponse;
import org.stockify.dto.response.TransactionResponse;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.SaleEntity;
import org.stockify.model.entity.TransactionEntity;
import org.stockify.model.enums.TransactionType;
import org.stockify.model.exception.InsufficientStockException;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.SaleMapper;
import org.stockify.model.mapper.TransactionMapper;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.SaleRepository;
import org.stockify.model.repository.UserRepository;
import org.stockify.model.specification.SaleSpecification;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleService {

    private final TransactionService transactionService;
    private final SaleMapper saleMapper;
    private final UserRepository clientRepository;
    private final SaleRepository saleRepository;
    private final TransactionMapper transactionMapper;
    private final ProductRepository productRepository;

    public SaleResponse createSale(SaleRequest request) {
        if (request.getTransaction() == null || request.getTransaction().getDetailTransactions() == null
                || request.getTransaction().getDetailTransactions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sale requires at least one product detail");
        }

        List<ProductEntity> productsToUpdate = new ArrayList<>();
        for (DetailTransactionRequest detail : request.getTransaction().getDetailTransactions()) {
            ProductEntity product = productRepository.findById(detail.getProductID())
                    .orElseThrow(() -> new NotFoundException("Product not found with ID " + detail.getProductID()));

            long quantity = detail.getQuantity();
            long currentStock = product.getStock() == null ? 0L : product.getStock();
            long currentSold = product.getSoldQuantity() == null ? 0L : product.getSoldQuantity();

            if (currentStock < quantity) {
                throw new InsufficientStockException(
                        "Insufficient stock for product ID " + detail.getProductID()
                );
            }

            product.setStock(currentStock - quantity);
            product.setSoldQuantity(currentSold + quantity);
            productsToUpdate.add(product);
        }

        TransactionEntity transaction = transactionService.createTransaction(
                request.getTransaction(), TransactionType.SALE
        );

        SaleEntity sale = saleMapper.toEntity(request);
        sale.setTransaction(transaction);

        if (request.getClientId() != null) {
            sale.setClient(clientRepository.findById(request.getClientId())
                    .orElseThrow(() -> new NotFoundException("Client not found with ID " + request.getClientId())));
        }

        productRepository.saveAll(productsToUpdate);
        SaleEntity saved = saleRepository.save(sale);
        SaleResponse saleResponse = saleMapper.toResponseDTO(saved);
        saleResponse.setTransaction(transactionMapper.toDto(transaction));
        return saleResponse;
    }

    public void delete(Long id) {
        if (!saleRepository.existsById(id)) {
            throw new NotFoundException("Sale with ID " + id + " not found");
        }
        saleRepository.deleteById(id);
    }

    public Page<SaleResponse> findAll(SaleFilterRequest filterRequest, Pageable pageable) {
        Specification<SaleEntity> specification = Specification
                .where(SaleSpecification.byClientId(filterRequest.getClientId()))
                .and(SaleSpecification.bySaleId(filterRequest.getSaleId()))
                .and(SaleSpecification.byTransactionId(filterRequest.getTransactionId()));

        Page<SaleEntity> saleEntities = saleRepository.findAll(specification, pageable);
        return saleEntities.map(saleMapper::toResponseDTO);
    }

    public SaleResponse findById(Long id) {
        SaleEntity saleEntity = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale with ID " + id + " not found"));
        return saleMapper.toResponseDTO(saleEntity);
    }

    public TransactionResponse findTransactionBySaleId(Long saleId) {
        SaleEntity saleEntity = saleRepository.findById(saleId)
                .orElseThrow(() -> new NotFoundException("Sale with ID " + saleId + " not found"));

        TransactionEntity transactionEntity = saleEntity.getTransaction();
        return transactionMapper.toDto(transactionEntity);
    }

    public SaleResponse updateSalePartial(Long id, SaleRequest saleRequest) {
        SaleEntity existingSale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale with ID " + id + " not found"));
        saleMapper.partialUpdateSaleEntity(saleRequest, existingSale);

        SaleEntity updatedSale = saleRepository.save(existingSale);
        return saleMapper.toResponseDTO(updatedSale);
    }

    public SaleResponse updateSaleFull(Long id, SaleRequest saleRequest) {
        SaleEntity existingSale = saleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Sale with ID " + id + " not found"));

        saleMapper.updateSaleEntity(saleRequest, existingSale);

        SaleEntity updatedSale = saleRepository.save(existingSale);
        return saleMapper.toResponseDTO(updatedSale);
    }
}



