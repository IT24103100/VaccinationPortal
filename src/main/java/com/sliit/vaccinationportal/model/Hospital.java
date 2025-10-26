package com.sliit.vaccinationportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "hospitals",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "city"}))
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Hospital name is required")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "District is required")
    private String district;

    @Column(nullable = false)
    @NotBlank(message = "City is required")
    private String city;

    private String address;

    @Pattern(regexp = "(\\d{3}-\\d{7})|(\\d{10})|^$",
            message = "Contact number must be in format XXX-XXXXXXX or 10 digits")
    private String contactNumber;
}