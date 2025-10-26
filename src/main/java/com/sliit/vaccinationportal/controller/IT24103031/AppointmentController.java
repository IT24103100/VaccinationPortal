package com.sliit.vaccinationportal.controller.IT24103031;

import com.sliit.vaccinationportal.model.Appointment;
import com.sliit.vaccinationportal.model.Hospital;
import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.service.IT24103031.AppointmentService;
import com.sliit.vaccinationportal.service.IT24103103.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Controller
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @GetMapping("/appointment/new")
    public String showAppointmentForm(Model model) {
        List<Hospital> hospitals = appointmentService.getAllHospitals();

        List<LocalTime> timeSlots = Arrays.asList(
                LocalTime.of(9, 0),   // 09:00 AM
                LocalTime.of(9, 30),  // 09:30 AM
                LocalTime.of(10, 0),  // 10:00 AM
                LocalTime.of(10, 30), // 10:30 AM
                LocalTime.of(11, 0),  // 11:00 AM
                LocalTime.of(11, 30)  // 11:30 AM
        );

        model.addAttribute("hospitals", hospitals);
        model.addAttribute("timeSlots", timeSlots);
        model.addAttribute("appointment", new Appointment());

        return "IT24103031/appointment-form";
    }

    @PostMapping("/appointment/save")
    public String saveAppointment(@ModelAttribute("appointment") Appointment appointment,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {

        User user = userService.findUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        appointment.setUser(user);

        appointmentService.saveAppointment(appointment);

        redirectAttributes.addFlashAttribute("successMessage",
                "Your appointment has been successfully booked! You can view its status in your profile.");

        return "redirect:/";
    }
}