package com.example.reviewsapp.controller;

import com.example.reviewsapp.mapper.Mapper;
import com.example.reviewsapp.model.Review;
import com.example.reviewsapp.repository.ProductRepository;
import com.example.reviewsapp.repository.ReviewRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private ReviewRepository reviewRepository;

    @Mock
    private Page<Review> reviewPage;

    @Mock
    private Pageable pageable;

    /*
    @Test
    @DisplayName("GET /reviews success")
    void testGetAllReviewsByProductIdSuccess() throws Exception {
        // Setup mocks
        Review review1 = new Review(1L, 5L, "Great desk", 5);
        Review review2 = new Review(2L, 5L, "The worst desk", 1);

        List<Review> reviews = Lists.newArrayList(review1, review2);
        Page<Review> pagedReviews = new PageImpl(reviews);
        when(reviewRepository.findAll(PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "rating")))).thenReturn(pagedReviews);

        // Execute the GET request
        mockMvc.perform(get("/reviews"))
                // Validate response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate header
                .andExpect(header().string(HttpHeaders.LOCATION, "/reviews"))
                // Validate returned fields
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].productId", is(5)))
                .andExpect(jsonPath("$[0].text", is("Great desk")))
                .andExpect(jsonPath("$[0].rating", is(5)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].productId", is(5)))
                .andExpect(jsonPath("$[1].text", is("The worst desk")))
                .andExpect(jsonPath("$[1].rating", is(1)));
    }
     */

    @Test
    @DisplayName("GET /reviews/1 success")
    void getReviewByIdSuccess() throws Exception {
        // Setup mocks
        Review review = new Review(1L, 5L, "Great desk", 5);

        doReturn(Optional.of(review)).when(reviewRepository).findById(1L);

        // Execute the GET request
        mockMvc.perform(get("/reviews/{id}", 1L))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/reviews/1"))
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                // Validate returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.productId", is(5)))
                .andExpect(jsonPath("$.text", is("Great desk")))
                .andExpect(jsonPath("$.rating", is(5)));
    }

    @Test
    @DisplayName("GET /reviews/1 - Not Found")
    void getReviewByIdNotFound() throws Exception {
        // Setup mocks
        doReturn(Optional.empty()).when(reviewRepository).findById(1L);

        // Execute the GET request
        mockMvc.perform(get("/reviews/{id}", 1L))
                // Validate the response code
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /reviews success")
    void createReviewSuccess() throws Exception {
        // Setup mocks
        doReturn(true).when(productRepository).existsById(any());
        Review review = new Review(null, 1L, "Great desk", 5);
        Review reviewToReturn = new Review(1L, 1L, "Great desk", 5);
        doReturn(reviewToReturn).when(reviewRepository).save(any());

        mockMvc.perform(post("/reviews")
                // Validate the response code and content type
                .contentType(MediaType.APPLICATION_JSON)
                .content(Mapper.asJsonString(reviewToReturn)))
                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/reviews/1"))
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.text", is("Great desk")))
                .andExpect(jsonPath("$.rating", is(5)));

    }

    @Test
    @DisplayName("POST /reviews not acceptable")
    void createReviewNotAcceptable() throws Exception {
        // Setup mocks
        Review review = new Review(null, 1L, "Great desk", 5);
        Review reviewToReturn = new Review(1L, 1L, "Great desk", 5);
        doReturn(reviewToReturn).when(reviewRepository).save(any());

        mockMvc.perform(post("/reviews")
                // Validate the response code and content type
                .contentType(MediaType.APPLICATION_JSON)
                .content(Mapper.asJsonString(reviewToReturn)))
                // Validate the response code and content type
                .andExpect(status().isNotAcceptable());

    }

    @Test
    @DisplayName("PUT /reviews/1 success")
    void updateReviewSuccess() throws Exception {
        // Setup mocks
        doReturn(true).when(productRepository).existsById(any());
        Review reviewToPut = new Review(1L, 1L, "Not that great desk", 3);
        Review reviewToReturnFindById = new Review(1L, 1L, "Great desk", 5);
        doReturn(Optional.of(reviewToReturnFindById)).when(reviewRepository).findById(1L);
        doReturn(reviewToPut).when(reviewRepository).save(any());

        mockMvc.perform(put("/reviews/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(Mapper.asJsonString(reviewToPut)))
                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Validate headers
                .andExpect(header().string(HttpHeaders.LOCATION, "/reviews/1"))
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.productId", is(1)))
                .andExpect(jsonPath("$.text", is("Not that great desk")))
                .andExpect(jsonPath("$.rating", is(3)));
    }

    @Test
    @DisplayName("PUT /reviews/1 not acceptable")
    void updateReviewNotFound() throws Exception {
        // Setup mocks
        Review reviewToPut = new Review(1L, 1L, "Not that great desk", 3);
        Review reviewToReturnFindById = new Review(1L, 1L, "Great desk", 5);
        doReturn(Optional.of(reviewToReturnFindById)).when(reviewRepository).findById(1L);
        doReturn(reviewToPut).when(reviewRepository).save(any());

        mockMvc.perform(put("/reviews/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.IF_MATCH, 1)
                        .content(Mapper.asJsonString(reviewToPut)))
                // Validate the response code and content type
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("PUT /reviews/1 not found")
    void updateReviewNotAcceptable() throws Exception {
        // Setup mocks
        Review reviewToPut = new Review(1L, 1L, "Not that great desk", 3);

        mockMvc.perform(put("/reviews/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(Mapper.asJsonString(reviewToPut)))
                // Validate the response code and content type
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /reviews/1 success")
    void deleteReview() throws Exception {
        mockMvc.perform(delete("/reviews/{id}", 1))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("DELETE /reviews success")
    void deleteAllReviews() throws Exception {
        mockMvc.perform(delete("/reviews"))
                .andExpect(status().isAccepted());
    }

    @Test
    @DisplayName("Check correct rating")
    void isRatingCorrectSuccess() {
        ReviewController controller = new ReviewController(reviewRepository, productRepository);
        assertTrue(controller.isRatingCorrect(5));
    }

    @Test
    @DisplayName("Check incorrect rating")
    void isRatingIncorrectSuccess() {
        ReviewController controller = new ReviewController(reviewRepository, productRepository);
        assertFalse(controller.isRatingCorrect(6));
    }
}