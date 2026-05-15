package com.FedericoFunes.app_service.repositories;

import com.FedericoFunes.app_service.entities.DummyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DummyRepository extends JpaRepository<DummyEntity, Long> {
}
