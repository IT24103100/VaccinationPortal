package com.sliit.vaccinationportal.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "vaccine_stocks")
public class VaccineStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String vaccineName;

    private String manufacturer;

    private int totalQuantity;

    private LocalDate lastUpdated;
}