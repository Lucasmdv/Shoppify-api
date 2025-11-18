package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stockify.dto.request.cart.CartRequest;
import org.stockify.dto.request.cart.DetailCartRequest;
import org.stockify.dto.response.CartResponse;
import org.stockify.model.entity.CartEntity;
import org.stockify.model.entity.CartItemEntity;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.UserEntity;
import org.stockify.model.exception.ClientNotFoundException;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.CartMapper;
import org.stockify.model.repository.CartRepository;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartResponse findByUserId(Long userId) {
        CartEntity cartEntity = cartRepository.findByClientId(userId)
                .orElseThrow(() -> new NotFoundException("Cart for client id " + userId + " not found"));
        return cartMapper.toResponse(cartEntity);
    }



    private UserEntity resolveClient(Long clientId) {
        return userRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException("Client with id " + clientId + " not found"));
    }

    private ProductEntity resolveProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
    }




}
