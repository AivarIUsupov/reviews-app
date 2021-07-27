package com.example.reviewsapp.controller;

import com.example.reviewsapp.model.Product;
import com.example.reviewsapp.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Product>> getAllProducts() {
        LOGGER.info("Getting all product resources");

        try {
            return ResponseEntity.ok()
                    .location(new URI("/products"))
                    .body(productRepository.findAll());
        } catch (Exception e) {
            LOGGER.error("Error occurred:", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
        LOGGER.info("Getting product resource with id = {}", id);

        return productRepository.findById(id)
                .map(product -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .eTag(Long.toString(id))
                                .location(new URI("/products/" + product.getId()))
                                .body(product);
                    } catch (URISyntaxException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Product> postProduct(@RequestBody Product product) {
        LOGGER.info("Received product: {}", product.getTitle());

        Product postedProduct = productRepository
                .save(new Product(null, product.getTitle()));

        try {

            return ResponseEntity
                    .created(new URI("/products/" + postedProduct.getId()))
                    .eTag(Long.toString(postedProduct.getId()))
                    .body(postedProduct);

        } catch (URISyntaxException e) {
            LOGGER.error("Error occurred:", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
