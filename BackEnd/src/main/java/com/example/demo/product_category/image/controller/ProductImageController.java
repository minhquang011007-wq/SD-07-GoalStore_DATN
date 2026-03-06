package com.example.demo.product_category.image.controller;

import com.example.demo.product_category.image.dto.ProductImageResponse;
import com.example.demo.product_category.image.dto.UpdateImageRequest;
import com.example.demo.product_category.image.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService imageService;

    @PostMapping(value = "/api/products/{productId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ProductImageResponse>> upload(
            @PathVariable Integer productId,
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(defaultValue = "true") boolean avatarFirst
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.upload(productId, files, avatarFirst));
    }

    @PutMapping("/api/images/{imageId}")
    public ProductImageResponse update(@PathVariable Integer imageId, @RequestBody UpdateImageRequest request) {
        return imageService.update(imageId, request);
    }

    @DeleteMapping("/api/images/{imageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer imageId) {
        imageService.delete(imageId);
    }

    @GetMapping("/api/products/{productId}/images")
    public List<ProductImageResponse> findByProduct(@PathVariable Integer productId) {
        return imageService.findByProduct(productId);
    }
}
