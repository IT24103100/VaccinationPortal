package com.sliit.vaccinationportal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String nic;

    @Column(nullable = false)
    private String password;

    private String address;

    private String postalCode;

    private LocalDate birthday;

    private String phoneNumber;

    private String resetPasswordToken;

    private LocalDateTime tokenExpiryDate;

    private int age;

    private String role;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @PrePersist
    public void onBeforeCreate() {

        if (this.birthday != null) {
            this.age = Period.between(this.birthday, LocalDate.now()).getYears();
        }

        this.createdDate = LocalDateTime.now();
    }
}