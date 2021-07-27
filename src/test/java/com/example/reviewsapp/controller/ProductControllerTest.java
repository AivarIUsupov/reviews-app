package com.example.reviewsapp.controller;

import com.example.reviewsapp.mapper.Mapper;
import com.example.reviewsapp.model.Product;
import com.example.reviewsapp.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @Test
    @DisplayName("GET /products success")
    void testGetAllProducts() throws Exception {
        // Setup mocks
        Product product1 = new Product(5L, "desk");
        Product product2 = new Product(6L, "chair");

        doReturn(Lists.newArrayList(product1, product2)).when(productRepository).findAll();

        // Execute the GET request
        mockMvc.perform(get("/products"))
                // Validate response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate header
                .andExpect(header().string(HttpHeaders.LOCATION, "/products"))
                // Validate returned fields
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(5)))
                .andExpect(jsonPath("$[0].title", is("desk")))
                .andExpect(jsonPath("$[1].id", is(6)))
                .andExpect(jsonPath("$[1].title", is("chair")));
    }

    @Test
    @DisplayName("GET /products/5 success")
    void testGetProductById() throws Exception {
        // Setup mocks
        Product product = new Product(5L, "desk");

        doReturn(Optional.of(product)).when(productRepository).findById(5L);

        // Execute the GET request
        mockMvc.perform(get("/products/{id}", 5L))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/5"))
                .andExpect(header().string(HttpHeaders.ETAG, "\"5\""))
                // Validate returned fields
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.title", is("desk")));
    }

    @Test
    @DisplayName("GET /products/1 - Not Found")
    void testGetProductByIdNotFound() throws Exception {
        // Setup mocks
        doReturn(Optional.empty()).when(productRepository).findById(1L);

        // Execute the GET request
        mockMvc.perform(get("/products/{id}", 1L))
                // Validate the response code
                .andExpect(status().isNotFound());
    }
    
    @Test
    @DisplayName("POST /products success")
    void testCreateWidget() throws Exception {
        // Setup our mocked service
        Product postedProduct = new Product(null, "desk");
        Product productToReturn = new Product(1L, "desk");
        doReturn(productToReturn).when(productRepository).save(any());

        // Execute the POST request
        mockMvc.perform(post("/products")
                // Validate the response code and content type
                .contentType(MediaType.APPLICATION_JSON)
                .content(Mapper.asJsonString(postedProduct)))
                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/products/1"))
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("desk")));
    }
}