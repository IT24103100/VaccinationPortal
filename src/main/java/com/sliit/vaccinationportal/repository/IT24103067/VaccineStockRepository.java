package com.sliit.vaccinationportal.repository.IT24103067;

import com.sliit.vaccinationportal.model.VaccineStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface VaccineStockRepository extends JpaRepository<VaccineStock, Long> {

    Optional<VaccineStock> findByVaccineName(String vaccineName);

    @Query("SELECT SUM(v.totalQuantity) FROM VaccineStock v")
    Long findTotalStockQuantity();

    @Query("SELECT v.vaccineName, v.totalQuantity FROM VaccineStock v WHERE v.totalQuantity <= 100 ORDER BY v.totalQuantity ASC")
    List<Object[]> findLowStockVaccines();

    @Query("SELECT v.vaccineName, v.totalQuantity FROM VaccineStock v ORDER BY v.totalQuantity DESC")
    List<Object[]> findStockByVaccineType();
}