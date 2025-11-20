package org.stockify.model.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stockify.dto.request.cart.CartItemRequest;
import org.stockify.dto.response.CartResponse;
import org.stockify.model.entity.CartEntity;
import org.stockify.model.entity.CartItemEntity;
import org.stockify.model.entity.ProductEntity;
import org.stockify.model.entity.UserEntity;
import org.stockify.model.exception.ClientNotFoundException;
import org.stockify.model.exception.DuplicatedUniqueConstraintException;
import org.stockify.model.exception.InsufficientStockException;
import org.stockify.model.exception.NotFoundException;
import org.stockify.model.mapper.CartMapper;
import org.stockify.model.repository.CartRepository;
import org.stockify.model.repository.ProductRepository;
import org.stockify.model.repository.UserRepository;
import java.util.HashSet;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartResponse findByUserId(Long userId) {
        CartEntity cartEntity = resolveCart(userId);
        return cartMapper.toResponse(cartEntity);
    }

    public void createUserCart(Long userId){
        if(!hasCart(userId)){
            UserEntity client = resolveClient(userId);
            CartEntity cart = new CartEntity();
            cart.setClient(client);
            cartRepository.save(cart);
            cartMapper.toResponse(cart);
        }
    }

    public CartResponse addItem(CartItemRequest itemRequest, Long userId){

        CartEntity cart = resolveCart(userId);
        ProductEntity product = resolveProduct(itemRequest.getProductId());
        if (cart.getProducts() == null) {
            cart.setProducts(new HashSet<>());
        }

        checkStock(product, itemRequest.getQuantity());

        CartItemEntity item = cart.getProducts().stream()
                .filter(ci -> ci.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        Long requestedQuantity = itemRequest.getQuantity();
        Long newTotalQuantity;

        if (item == null) {
            newTotalQuantity = requestedQuantity;
            item = new CartItemEntity();
            item.setCart(cart);
            item.setProduct(product);

        } else {
            newTotalQuantity = item.getQuantity() + requestedQuantity;
        }

        checkStock(product, newTotalQuantity);
        item.setQuantity(newTotalQuantity);

        if (item.getId() == null) {
            cart.getProducts().add(item);
        }
        return cartMapper.toResponse(cartRepository.save(cart));
    }

    public CartResponse clearCart(Long userId){
       CartEntity cart =  resolveCart(userId);
       cart.getProducts().clear();
       return cartMapper.toResponse(cartRepository.save(cart));
    }

    public CartResponse updateItem(Long itemId, Long userId, Long quantity){
        CartEntity cart = resolveCart(userId);
        CartItemEntity item = cart.getProducts().stream()
                .filter(ci -> ci.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item with id " + itemId + " not found"));
        item.setQuantity(quantity);
        checkStock(item.getProduct(), quantity);
        return cartMapper.toResponse(cartRepository.save(cart));
    }

    public CartResponse removeItem(Long itemId, Long userId){
        CartEntity cart = resolveCart(userId);
        CartItemEntity item = cart.getProducts().stream()
                .filter(ci -> ci.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Cart item with id " + itemId + " not found"));
        cart.getProducts().remove(item);
        return cartMapper.toResponse(cartRepository.save(cart));
    }


    //Helpers
    private boolean hasCart(Long userId) {
        if(cartRepository.findByClientId(userId).isPresent()){
            throw new DuplicatedUniqueConstraintException("Cart for client id " + userId + " already exists");
        }
        return false;
    }

    private ProductEntity resolveProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id " + productId + " not found"));
    }

    private void checkStock(ProductEntity product, Long quantity) {
        Long stock = product.getStock();
        if (stock == null || quantity == null || stock < quantity) {
            throw new InsufficientStockException("Insufficient stock for product id " + product.getId());
        }
    }

    private CartEntity resolveCart(Long userId) {
        return cartRepository.findByClientId(userId)
                .orElseThrow(() -> new NotFoundException("Cart for client id " + userId + " not found"));
    }

    private UserEntity resolveClient(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ClientNotFoundException("Client with id " + userId + " not found"));
    }








}
