package com.sliit.vaccinationportal.controller.IT24103048;

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
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewRepository reviewRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model, Principal principal) {
        model.addAttribute("adminName", principal.getName());
        return "IT24103048/admin-dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<User> userList = userService.searchUsers(keyword);
        model.addAttribute("users", userList);
        model.addAttribute("keyword", keyword);
        return "IT24103048/admin-manage-users";
    }

    @GetMapping("/users/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "IT24103048/admin-add-user";
    }

    @PostMapping("/users/save")
    public String saveUserByAdmin(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        if (userService.findUserByEmail(user.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "A user with email " + user.getEmail() + " already exists.");
            return "redirect:/admin/users/add";
        }
        if (userService.findUserByNic(user.getNic()).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "A user with NIC " + user.getNic() + " already exists.");
            return "redirect:/admin/users/add";
        }
        userService.createUserByAdmin(user);
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable("id") Long userId, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id: " + userId));
        model.addAttribute("user", user);
        return "IT24103048/admin-edit-user";
    }

    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            // Check if email is being changed to one that already exists for a different user
            Optional<User> existingUserWithEmail = userService.findUserByEmail(user.getEmail());
            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "A user with email " + user.getEmail() + " already exists.");
                return "redirect:/admin/users/edit/" + user.getId();
            }
            
            // Check if NIC is being changed to one that already exists for a different user
            Optional<User> existingUserWithNic = userService.findUserByNic(user.getNic());
            if (existingUserWithNic.isPresent() && !existingUserWithNic.get().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("errorMessage", "A user with NIC " + user.getNic() + " already exists.");
                return "redirect:/admin/users/edit/" + user.getId();
            }
            
            userService.saveExistingUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            return "redirect:/admin/users";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating user: " + e.getMessage());
            return "redirect:/admin/users/edit/" + user.getId();
        }
    }

    @PostMapping("/users/change-role")
    public String changeUserRole(@RequestParam("userId") Long userId,
                                 @RequestParam("newRole") String newRole,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + userId));
        user.setRole(newRole);
        userService.saveExistingUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "User role updated successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long userId, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(userId);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/reviews")
    public String manageReviews(Model model) {
        List<Review> allReviews = reviewRepository.findAllByOrderByReviewDateDesc();
        model.addAttribute("reviews", allReviews);
        return "IT24103048/admin-manage-reviews";
    }

    @GetMapping("/reviews/delete/{id}")
    public String deleteReview(@PathVariable("id") Long reviewId, RedirectAttributes redirectAttributes) {
        reviewRepository.deleteById(reviewId);
        redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully!");
        return "redirect:/admin/reviews";
    }
}