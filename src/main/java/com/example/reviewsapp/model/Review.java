package com.example.reviewsapp.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "reviews")
public class Review implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "productId")
    private Long productId;

    @Column(name = "text")
    private String text;

    @Column(name = "rating")
    private int rating;

    @Column(name = "isDeleted")
    private boolean isDeleted = Boolean.FALSE;


    public Review() {
    }

    public Review(Long id, Long productId, String text, int rating) {
        this.id = id;
        this.productId = productId;
        this.text = text;
        this.rating = rating;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", productId=" + productId +
                ", text='" + text + '\'' +
                ", rating=" + rating +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
