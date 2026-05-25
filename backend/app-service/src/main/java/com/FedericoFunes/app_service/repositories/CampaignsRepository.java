package com.FedericoFunes.app_service.repositories;

import com.FedericoFunes.app_service.entities.CampaignsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignsRepository extends JpaRepository<CampaignsEntity, Long> {
}

