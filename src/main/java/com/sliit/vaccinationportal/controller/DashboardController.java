package com.sliit.vaccinationportal.controller;

import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.service.IT24103103.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

@Controller
public class DashboardController {

    @Autowired
    private UserService userService;


    @GetMapping("/nurse/dashboard")
    public String nurseDashboard(Model model, Principal principal) {
        User nurse = userService.findUserByEmail(principal.getName()).get();
        model.addAttribute("nurseName", nurse.getName());
        model.addAttribute("activePage", "dashboard");
        return "nurse-dashboard";
    }
}