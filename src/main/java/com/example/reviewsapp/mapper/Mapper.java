package com.example.reviewsapp.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Mapper {
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
