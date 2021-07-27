package com.example.reviewsapp.controller;

import com.example.reviewsapp.model.Review;
import com.example.reviewsapp.repository.ProductRepository;
import com.example.reviewsapp.repository.ReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    public ReviewController(ReviewRepository reviewRepository, ProductRepository productRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<Review>> getAllReviewsByProductId(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size,
                                                                 @RequestParam(required = false) Long productId,
                                                                 @RequestParam(defaultValue = "false") boolean showDeleted) {

        LOGGER.info("Getting all review resources");

        Page<Review> reviews;

        if (productId == null) {
            if (!showDeleted) {
                LOGGER.info("For all products");

                reviews = reviewRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating")));
            } else {
                LOGGER.info("For all products (deleted reviews)");

                reviews = reviewRepository.findDeletedReviews(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating")));
            }
        } else {
            if (!showDeleted) {
                LOGGER.info("For product with id = {}", productId);

                reviews = reviewRepository.findAllByProductId(productId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating")));
            } else {
                LOGGER.info("For product with id = {} (deleted reviews)", productId);

                reviews = reviewRepository.findAllDeletedReviewsByProductId(productId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating")));
            }
        }

        if (reviews.isEmpty()) {
            LOGGER.info("No review resources were found");

            return ResponseEntity.notFound().build();
        }

        LOGGER.info("Reviews found: {}", reviews);

        try {
            return ResponseEntity.ok()
                    .location(new URI("/reviews"))
                    .body(reviews);
        } catch (Exception e) {
            LOGGER.error("Error occurred:", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getReviewById(@PathVariable("id") Long id) {
        LOGGER.info("Getting review resource with id = {}", id);

        return reviewRepository.findById(id)
                .map(review -> {
                    try {
                        return ResponseEntity
                                .ok()
                                .eTag(Long.toString(id))
                                .location(new URI("/reviews/" + review.getId()))
                                .body(review);
                    } catch (URISyntaxException e) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createReview(@RequestBody Review review) {
        LOGGER.info("Posting review resource");

        if (!isRatingCorrect(review.getRating())) {
            LOGGER.warn("Can not post review resource with an incorrect rating");

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        if (productRepository.existsById(review.getProductId())) {
            Review postedReview = reviewRepository
                    .save(new Review(null, review.getProductId(), review.getText(), review.getRating()));

            try {
                return ResponseEntity
                        .created(new URI("/reviews/" + postedReview.getId()))
                        .eTag(Long.toString(postedReview.getId()))
                        .body(postedReview);
            } catch (URISyntaxException e) {
                LOGGER.error("Error occurred:", e);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

        } else {
            LOGGER.warn("Can not post review resource for non-existent product resource");

            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }

    @PutMapping(value = "{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Review> updateReview(@PathVariable("id") Long id, @RequestBody Review review) {
        LOGGER.info("Updating review resource with id = {}", id);
        Optional<Review> reviewData = reviewRepository.findById(id);

        if (reviewData.isPresent()) {

            if (productRepository.existsById(review.getProductId())) {
                if (!isRatingCorrect(review.getRating())) {
                    LOGGER.warn("Can not update review resource with an incorrect rating");

                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
                }

                Review updatedReview = reviewData.get();

                updatedReview.setProductId(review.getProductId());
                updatedReview.setText(review.getText());
                updatedReview.setRating(review.getRating());

                reviewRepository.save(updatedReview);

                try {
                    return ResponseEntity
                            .ok()
                            .eTag(Long.toString(updatedReview.getId()))
                            .location(new URI("/reviews/" + updatedReview.getId()))
                            .body(updatedReview);
                } catch (URISyntaxException e) {
                    LOGGER.error("Error occurred:", e);

                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
            } else {
                LOGGER.warn("Can not update review resource with non-existent product resource id");

                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
            }
        } else {
            LOGGER.info("Review resource with id = {} not found", id);

            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping(value = "{id}")
    public ResponseEntity<HttpStatus> deleteReview(@PathVariable("id") Long id) {
        LOGGER.info("Deleting review resource with id = {}", id);

        reviewRepository.deleteById(id);

        LOGGER.info("Review resource {} has been successfully deleted", id);

        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> deleteAllReviews() {
        LOGGER.info("Deleting all review resources");

        reviewRepository.deleteAll();

        LOGGER.info("All review resources has been successfully deleted");

        return ResponseEntity.accepted().build();
    }

    public boolean isRatingCorrect(int rating) {
        return (rating >= 0) && (rating <= 5);
    }

}
