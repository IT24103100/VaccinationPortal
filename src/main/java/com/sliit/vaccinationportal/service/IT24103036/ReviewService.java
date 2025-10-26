package com.sliit.vaccinationportal.service.IT24103036;

import com.sliit.vaccinationportal.model.Review;
import com.sliit.vaccinationportal.repository.IT24103036.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    public List<Review> getLatest5Reviews() {
        return reviewRepository.findTop5ByOrderByReviewDateDesc();
    }
}