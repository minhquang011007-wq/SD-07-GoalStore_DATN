package com.example.demo.cart.service;

import com.example.demo.cart.dto.CartItemRequest;
import com.example.demo.cart.dto.CartItemResponse;
import com.example.demo.cart.dto.CartResponse;
import com.example.demo.cart.entity.Cart;
import com.example.demo.cart.entity.CartItem;
import com.example.demo.cart.repository.CartItemRepository;
import com.example.demo.cart.repository.CartRepository;
import com.example.demo.customer.entity.Customer;
import com.example.demo.customer.repository.CustomerRepository;
import com.example.demo.product_category.variant.entity.ProductVariant;
import com.example.demo.product_category.variant.repository.ProductVariantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductVariantRepository productVariantRepository;

    public CartService(CartRepository cartRepository,
                       CartItemRepository cartItemRepository,
                       CustomerRepository customerRepository,
                       ProductVariantRepository productVariantRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.customerRepository = customerRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Transactional
    public CartResponse getCart(Integer customerId) {
        Cart cart = getOrCreateCart(customerId);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(CartItemRequest request) {
        Cart cart = getOrCreateCart(request.getCustomerId());
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy variant"));

        int quantity = request.getQuantity() == null || request.getQuantity() <= 0 ? 1 : request.getQuantity();
        Optional<CartItem> existing = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId());
        CartItem item = existing.orElseGet(CartItem::new);
        item.setCart(cart);
        item.setVariant(variant);
        item.setQuantity(existing.map(cartItem -> cartItem.getQuantity() + quantity).orElse(quantity));
        item.setUnitPrice(variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice());
        cartItemRepository.save(item);
        return toResponse(getOrCreateCart(request.getCustomerId()));
    }

    @Transactional
    public CartResponse updateItem(Integer itemId, CartItemRequest request) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cart item"));
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new RuntimeException("Số lượng phải lớn hơn 0");
        }
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return toResponse(item.getCart());
    }

    @Transactional
    public CartResponse removeItem(Integer itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cart item"));
        Cart cart = item.getCart();
        cartItemRepository.delete(item);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse clear(Integer customerId) {
        Cart cart = getOrCreateCart(customerId);
        cart.getItems().clear();
        cartRepository.save(cart);
        return toResponse(cart);
    }

    private Cart getOrCreateCart(Integer customerId) {
        return cartRepository.findByCustomerId(customerId).orElseGet(() -> {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
            Cart cart = new Cart();
            cart.setCustomer(customer);
            cart.setItems(new ArrayList<>());
            return cartRepository.save(cart);
        });
    }

    private CartResponse toResponse(Cart cart) {
        Cart managedCart = cartRepository.findById(cart.getId()).orElse(cart);
        CartResponse response = new CartResponse();
        response.setCartId(managedCart.getId());
        response.setCustomerId(managedCart.getCustomer().getId());
        response.setItems(managedCart.getItems().stream().map(item -> {
            CartItemResponse itemResponse = new CartItemResponse();
            itemResponse.setItemId(item.getId());
            itemResponse.setVariantId(item.getVariant().getId());
            itemResponse.setSku(item.getVariant().getSku());
            itemResponse.setProductName(item.getVariant().getProduct().getName());
            itemResponse.setSize(item.getVariant().getSize());
            itemResponse.setColor(item.getVariant().getColor());
            itemResponse.setQuantity(item.getQuantity());
            itemResponse.setUnitPrice(item.getUnitPrice());
            itemResponse.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            return itemResponse;
        }).toList());
        response.setTotalAmount(response.getItems().stream()
                .map(CartItemResponse::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return response;
    }
}
