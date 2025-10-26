package com.sliit.vaccinationportal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vaccination_records")
public class VaccinationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String vaccineName;

    private String dose;

    @PastOrPresent(message = "Vaccination date cannot be in the future.")
    private LocalDate vaccinationDate;

    private String administeredBy;

    // --- Standardized additional fields ---
    @Size(max = 64)
    private String batchNumber;

    @Size(max = 64)
    private String administrationSite; // e.g., Left Deltoid

    @Size(max = 500)
    private String notes; // internal notes

    // --- Audit metadata ---
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @NotBlank
    private String createdBy; // nurse email or id

    private String updatedBy; // last editor


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        if (this.updatedBy == null) {
            this.updatedBy = this.createdBy;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}