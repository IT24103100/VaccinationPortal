package com.sliit.vaccinationportal.controller;

import com.sliit.vaccinationportal.model.Review;
import com.sliit.vaccinationportal.service.IT24103036.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/")
    public String publicHomePage(Model model) {
        List<Review> latestReviews = reviewService.getLatest5Reviews();
        model.addAttribute("latestReviews", latestReviews);
        return "home";
    }
}