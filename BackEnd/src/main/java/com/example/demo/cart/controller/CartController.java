package com.example.demo.cart.controller;

import com.example.demo.cart.dto.CartItemRequest;
import com.example.demo.cart.dto.CartResponse;
import com.example.demo.cart.service.CartService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public CartResponse getCart(@RequestParam Integer customerId) {
        return cartService.getCart(customerId);
    }

    @PostMapping("/items")
    public CartResponse addItem(@RequestBody CartItemRequest request) {
        return cartService.addItem(request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(@PathVariable Integer itemId, @RequestBody CartItemRequest request) {
        return cartService.updateItem(itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(@PathVariable Integer itemId) {
        return cartService.removeItem(itemId);
    }

    @DeleteMapping("/clear")
    public CartResponse clear(@RequestParam Integer customerId) {
        return cartService.clear(customerId);
    }
}
