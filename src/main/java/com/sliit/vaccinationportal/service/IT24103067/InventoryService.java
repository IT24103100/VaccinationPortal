package com.sliit.vaccinationportal.service.IT24103067;

import com.sliit.vaccinationportal.model.VaccineStock;
import com.sliit.vaccinationportal.repository.IT24103067.VaccineStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private VaccineStockRepository stockRepository;

    public List<VaccineStock> getAllStocks() {
        return stockRepository.findAll();
    }

    public VaccineStock findStockById(Long stockId) {
        return stockRepository.findById(stockId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid stock ID: " + stockId));
    }

    public Optional<VaccineStock> findByVaccineName(String name) {
        return stockRepository.findByVaccineName(name);
    }

    public void addNewVaccine(VaccineStock vaccineStock) {
        // Validate minimal required fields
        if (vaccineStock.getVaccineName() == null || vaccineStock.getVaccineName().trim().isEmpty()) {
            throw new IllegalArgumentException("Vaccine name is required");
        }
        if (vaccineStock.getTotalQuantity() < 0) {
            // Prevent negative stock from being persisted
            vaccineStock.setTotalQuantity(0);
        }
        vaccineStock.setLastUpdated(LocalDate.now());
        stockRepository.save(vaccineStock);
    }

    public void updateStockQuantity(Long stockId, int quantityToAdd) {
        VaccineStock stock = findStockById(stockId);
        int newQuantity = stock.getTotalQuantity() + quantityToAdd;
        if (newQuantity < 0) {
            // Guard: never allow negative quantities
            newQuantity = 0;
        }
        stock.setTotalQuantity(newQuantity);
        stock.setLastUpdated(LocalDate.now());
        stockRepository.save(stock);
    }

    public void updateStock(VaccineStock stockDetails) {
        if (stockDetails.getId() == null) {
            throw new IllegalArgumentException("Stock ID is required for update");
        }
        VaccineStock existingStock = findStockById(stockDetails.getId());
        if (stockDetails.getVaccineName() == null || stockDetails.getVaccineName().trim().isEmpty()) {
            throw new IllegalArgumentException("Vaccine name is required");
        }
        int safeQuantity = Math.max(0, stockDetails.getTotalQuantity());
        existingStock.setVaccineName(stockDetails.getVaccineName());
        existingStock.setManufacturer(stockDetails.getManufacturer());
        existingStock.setTotalQuantity(safeQuantity);
        existingStock.setLastUpdated(LocalDate.now());
        stockRepository.save(existingStock);
    }

    /**
     * Deletes a vaccine stock from the database using its ID.
     */
    public void deleteVaccine(Long stockId) {
        // Ensure the stock exists; throws if not
        findStockById(stockId);
        stockRepository.deleteById(stockId);
    }

    /**
     * Decrements the stock of a given vaccine by one after it has been administered.
     * Finds the vaccine by its unique name.
     * @param vaccineName The name of the vaccine to be decremented.
     */
    public void decrementStockByName(String vaccineName) {
        VaccineStock stock = stockRepository.findByVaccineName(vaccineName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vaccine name for stock update: " + vaccineName));
        if (stock.getTotalQuantity() <= 0) {
            throw new IllegalStateException("Cannot decrement. Vaccine '" + vaccineName + "' has no stock.");
        }
        stock.setTotalQuantity(stock.getTotalQuantity() - 1);
        stock.setLastUpdated(LocalDate.now());
        stockRepository.save(stock);
    }

    /**
     * Increments the stock of a given vaccine by one, e.g., when a record is corrected.
     * Finds the vaccine by its unique name.
     * @param vaccineName The name of the vaccine to be incremented.
     */
    public void incrementStockByName(String vaccineName) {
        VaccineStock stock = stockRepository.findByVaccineName(vaccineName)
                .orElseThrow(() -> new IllegalArgumentException("Invalid vaccine name for stock update: " + vaccineName));
        stock.setTotalQuantity(stock.getTotalQuantity() + 1);
        stock.setLastUpdated(LocalDate.now());
        stockRepository.save(stock);
    }

    /**
     * Checks if a vaccine is available in stock.
     * @param vaccineName The name of the vaccine.
     * @return true if stock exists and quantity is greater than 0, false otherwise.
     */
    public boolean isStockAvailable(String vaccineName) {
        Optional<VaccineStock> stockOptional = stockRepository.findByVaccineName(vaccineName);
        return stockOptional.map(stock -> stock.getTotalQuantity() > 0).orElse(false);
    }
}