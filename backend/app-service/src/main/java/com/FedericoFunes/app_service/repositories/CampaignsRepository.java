package com.FedericoFunes.app_service.repositories;

import com.FedericoFunes.app_service.entities.CampaignsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CampaignsRepository extends JpaRepository<CampaignsEntity, Long> {

    @Query("SELECT c.id, c.title, COUNT(sd) FROM campaigns c LEFT JOIN c.subscribedDonors sd WHERE c.isActive = true AND c.endDate IS NULL GROUP BY c.id, c.title")
    List<Object[]> countSubscribersPerActiveCampaign();

    @Query("SELECT c.id, c.title, COUNT(sd) FROM campaigns c LEFT JOIN c.subscribedDonors sd WHERE c.isActive = true AND c.endDate IS NOT NULL GROUP BY c.id, c.title")
    List<Object[]> countSubscribersPerFinishedCampaign();

    @Query("SELECT COUNT(sd) FROM campaigns c LEFT JOIN c.subscribedDonors sd")
    Long countTotalSubscribers();

    @Query("SELECT COUNT(c) FROM campaigns c")
    Long countTotalCampaigns();

    @Query("SELECT d.bloodGroup, d.bloodFactor, COUNT(d) FROM campaigns c JOIN c.subscribedDonors d WHERE c.id = :campaignId GROUP BY d.bloodGroup, d.bloodFactor")
    List<Object[]> countBloodTypesByCampaign(@Param("campaignId") Long campaignId);
    @Query("SELECT COUNT(sd) FROM campaigns c LEFT JOIN c.subscribedDonors sd WHERE c.endDate IS NOT NULL")
    Long countTotalSubscribersInFinishedCampaigns();

    @Query("SELECT COUNT(c) FROM campaigns c WHERE c.endDate IS NOT NULL")
    Long countFinishedCampaigns();

    List<CampaignsEntity> findByCreatorIdAndIsActiveTrueAndEndDateIsNull(Long organizerId);
}

