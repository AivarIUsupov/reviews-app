package com.example.reviewsapp.repository;

import com.example.reviewsapp.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select e from #{#entityName} e where e.isDeleted = false and e.productId = ?1")
    @Transactional(readOnly = true)
    Page<Review> findAllByProductId(Long productId, Pageable pageable);

    @Query("select e from #{#entityName} e where e.isDeleted = true and e.productId = ?1")
    @Transactional(readOnly = true)
    Page<Review> findAllDeletedReviewsByProductId(Long productId, Pageable pageable);

    @Override
    @Transactional(readOnly = true)
    @Query("select e from #{#entityName} e where e.isDeleted = false")
    Page<Review> findAll(Pageable pageable);

    //Look up for deleted entities
    @Query("select e from #{#entityName} e where e.isDeleted = true")
    @Transactional(readOnly = true)
    Page<Review> findDeletedReviews(Pageable pageable);

    @Override
    @Query("update #{#entityName} e set e.isDeleted = true where e.id = ?1")
    @Transactional
    @Modifying
    void deleteById(Long id);

    @Override
    @Query("update #{#entityName} e set e.isDeleted = true")
    @Transactional
    @Modifying
    void deleteAll();

}
