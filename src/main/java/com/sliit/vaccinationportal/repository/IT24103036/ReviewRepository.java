package com.sliit.vaccinationportal.repository.IT24103036;

import com.sliit.vaccinationportal.model.Review;
import com.sliit.vaccinationportal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findAllByOrderByReviewDateDesc();
    List<Review> findTop5ByOrderByReviewDateDesc();

    // New method to find all reviews by a specific user
    List<Review> findAllByUserOrderByReviewDateDesc(User user);

    void deleteByUserId(Long userId);
}