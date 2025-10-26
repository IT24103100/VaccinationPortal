package com.sliit.vaccinationportal.service.IT24103100;

import com.sliit.vaccinationportal.model.User;
import com.sliit.vaccinationportal.model.VaccinationRecord;
import com.sliit.vaccinationportal.repository.IT24103100.VaccinationRecordRepository;
import com.sliit.vaccinationportal.service.IT24103067.InventoryService;
import com.sliit.vaccinationportal.service.IT24103103.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for creating, updating, retrieving and deleting vaccination records.
 * Also coordinates vaccine stock adjustments with the inventory service.
 */
@Service
public class VaccinationService {

    private final VaccinationRecordRepository recordRepository;
    private final InventoryService inventoryService;
    private final UserService userService;

    public VaccinationService(VaccinationRecordRepository recordRepository,
                              InventoryService inventoryService,
                              UserService userService) {
        // Constructor injection for required repositories/services
        this.recordRepository = recordRepository;
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    /**
     * Creates a new vaccination record for a given user and decrements the stock.
     */
    @Transactional
    public void createVaccinationRecord(Long userId, VaccinationRecord record) {
        // Server-side stock validation
        if (!inventoryService.isStockAvailable(record.getVaccineName())) {
            throw new IllegalStateException("Cannot create record. Vaccine '" + record.getVaccineName() + "' is out of stock.");
        }

        User patient = userService.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Patient ID: " + userId));

        record.setUser(patient);
        recordRepository.save(record);
        inventoryService.decrementStockByName(record.getVaccineName());
    }

    /**
     * Retrieves the complete vaccination history for a specific user.
     */
    public List<VaccinationRecord> getHistoryForUser(Long userId) {
        return recordRepository.findByUserIdOrderByVaccinationDateDesc(userId);
    }

    /**
     * Finds a single vaccination record by its unique ID.
     */
    public VaccinationRecord findRecordById(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Record ID: " + recordId));
    }

    /**
     * Updates an existing vaccination record with new details.
     * If the vaccine name changes, increment stock for the old vaccine and decrement for the new one.
     */
    @Transactional
    public VaccinationRecord updateVaccinationRecord(VaccinationRecord updatedRecord) {
        // Find the original record to get the old vaccine name
        VaccinationRecord existingRecord = findRecordById(updatedRecord.getId());
        String originalVaccineName = existingRecord.getVaccineName();

        // Update the record's details
        existingRecord.setVaccineName(updatedRecord.getVaccineName());
        existingRecord.setDose(updatedRecord.getDose());
        existingRecord.setVaccinationDate(updatedRecord.getVaccinationDate());
        VaccinationRecord savedRecord = recordRepository.save(existingRecord);

        // Adjust stock if the vaccine name has changed
        String newVaccineName = savedRecord.getVaccineName();
        if (!originalVaccineName.equals(newVaccineName)) {
            inventoryService.incrementStockByName(originalVaccineName);
            inventoryService.decrementStockByName(newVaccineName);
        }

        return savedRecord;
    }

    /**
     * Deletes a vaccination record and increments the corresponding vaccine stock.
     */
    @Transactional
    public void deleteVaccinationRecord(Long recordId) {
        // Find the record to get the vaccine name before deleting
        VaccinationRecord record = findRecordById(recordId);
        String vaccineName = record.getVaccineName();

        // Delete the record
        recordRepository.deleteById(recordId);

        // Increment the stock for the vaccine from the deleted record
        inventoryService.incrementStockByName(vaccineName);
    }

    /**
     * Checks if a specific dose of a vaccine has already been administered to a user.
     */
    public boolean isDoseAlreadyAdministered(Long userId, String vaccineName, String dose) {
        return recordRepository.existsByUserIdAndVaccineNameAndDose(userId, vaccineName, dose);
    }

    /**
     * Checks if a specific dose of a vaccine has already been administered to a user,
     * excluding a given record ID. Useful when updating a record.
     */
    public boolean isDoseAlreadyAdministeredForAnotherRecord(Long userId, String vaccineName, String dose, Long recordId) {
        return recordRepository.existsByUserIdAndVaccineNameAndDoseAndIdNot(userId, vaccineName, dose, recordId);
    }
}