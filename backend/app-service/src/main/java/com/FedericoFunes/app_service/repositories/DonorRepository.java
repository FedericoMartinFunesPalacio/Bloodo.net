package com.FedericoFunes.app_service.repositories;

import com.FedericoFunes.app_service.entities.DonorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DonorRepository extends JpaRepository<DonorEntity, Long> {

    @Query("SELECT d.bloodGroup, d.bloodFactor, COUNT(d) FROM donor d WHERE d.isActive = true GROUP BY d.bloodGroup, d.bloodFactor ORDER BY COUNT(d) DESC")
    List<Object[]> countBloodTypesGlobally();

    @Query("SELECT COUNT(d) FROM donor d WHERE d.isActive = true")
    Long countActiveDonors();

    @Query("SELECT COUNT(c) FROM campaigns c JOIN c.subscribedDonors d WHERE d.id = :donorId AND c.endDate IS NOT NULL AND c.isActive = true")
    Long countFinishedCampaignsByDonor(@Param("donorId") Long donorId);

    @Query("SELECT c.endDate FROM campaigns c JOIN c.subscribedDonors d WHERE d.id = :donorId AND c.endDate IS NOT NULL AND c.isActive = true ORDER BY c.endDate DESC")
    List<LocalDate> findFinishedCampaignEndDatesByDonor(@Param("donorId") Long donorId);
}
