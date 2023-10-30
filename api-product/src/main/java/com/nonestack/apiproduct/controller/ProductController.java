package com.nonestack.apiproduct.controller;

import com.nonestack.apiproduct.domain.Product;
import com.nonestack.apiproduct.dto.ProductFORM;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.nonestack.apiproduct.repository.ProductRepository;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProductController {

    private final ProductRepository productRepository;

    @GetMapping("/products")
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Product> findProductById(@PathVariable Long productId) {
        return productRepository.findById(productId)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id : " + productId));
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody ProductFORM productFORM) {

        if(!StringUtils.hasText(productFORM.getTitle())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product title is required ");
        }

        if(productFORM.getPrice() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product price should not be 0.00");
        }

        Product product = new Product();
        product.setTitle(productFORM.getTitle());
        product.setPrice(productFORM.getPrice());
        product.setDescription(productFORM.getDescription());

        Product savedProduct = productRepository.save(product);

        return ResponseEntity.created(URI.create("/products/" + savedProduct.getId())).body(savedProduct);
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<?> deleteProductById(@PathVariable Long productId) {
        return productRepository.findById(productId)
            .map((product -> {
                productRepository.delete(product);
                return ResponseEntity.ok(product);
            }))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with id : " + productId));
    }

}
