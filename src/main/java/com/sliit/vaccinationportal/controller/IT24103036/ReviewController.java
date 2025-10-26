package com.sliit.vaccinationportal.controller.IT24103036;

import com.sliit.vaccinationportal.model.Review;
import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.repository.IT24103036.ReviewRepository;
import com.sliit.vaccinationportal.service.IT24103103.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @GetMapping("/review")
    public String showReviewForm(Model model, Principal principal) {
        String email = principal.getName();
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        // Get all reviews by this user
        List<Review> userReviews = reviewRepository.findAllByUserOrderByReviewDateDesc(user);
        model.addAttribute("userReviews", userReviews);
        model.addAttribute("review", new Review());
        return "IT24103036/review-form";
    }

    @PostMapping("/review/save")
    public String saveReview(@ModelAttribute("review") Review review,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        review.setUser(user);
        review.setReviewDate(LocalDate.now());
        reviewRepository.save(review);

        redirectAttributes.addFlashAttribute("reviewSuccess", "Thank you for your review! Your feedback is valuable to us.");
        return "redirect:/review";
    }

    @GetMapping("/review/edit/{id}")
    public String showEditReviewForm(@PathVariable("id") Long reviewId,
                                     Model model,
                                     Principal principal,
                                     RedirectAttributes redirectAttributes) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + reviewId));

        // Security check: ensure the logged-in user owns this review
        String email = principal.getName();
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only edit your own reviews.");
            return "redirect:/review";
        }

        model.addAttribute("reviewToEdit", review);
        return "IT24103036/review-edit-form";
    }

    @PostMapping("/review/update")
    public String updateReview(@ModelAttribute("reviewToEdit") Review review,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        Review existingReview = reviewRepository.findById(review.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + review.getId()));

        // Security check
        String email = principal.getName();
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        if (!existingReview.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only edit your own reviews.");
            return "redirect:/review";
        }

        existingReview.setRating(review.getRating());
        existingReview.setComment(review.getComment());
        existingReview.setReviewDate(LocalDate.now());
        reviewRepository.save(existingReview);

        redirectAttributes.addFlashAttribute("reviewSuccess", "Your review has been updated successfully!");
        return "redirect:/review";
    }

    @GetMapping("/review/delete/{id}")
    public String deleteReview(@PathVariable("id") Long reviewId,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid review Id:" + reviewId));

        // Security check
        String email = principal.getName();
        User user = userService.findUserByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You can only delete your own reviews.");
            return "redirect:/review";
        }

        reviewRepository.deleteById(reviewId);
        redirectAttributes.addFlashAttribute("reviewSuccess", "Your review has been deleted successfully!");
        return "redirect:/review";
    }
}