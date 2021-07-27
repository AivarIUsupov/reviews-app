package com.example.reviewsapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReviewsAppApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReviewsAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ReviewsAppApplication.class, args);

        LOGGER.info("Reviews Application has started");
    }

}
