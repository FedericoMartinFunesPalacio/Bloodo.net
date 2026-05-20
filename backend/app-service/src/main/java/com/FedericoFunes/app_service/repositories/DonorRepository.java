package com.FedericoFunes.app_service.repositories;

import com.FedericoFunes.app_service.entities.DonorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonorRepository extends JpaRepository<DonorEntity, Long> {
}
