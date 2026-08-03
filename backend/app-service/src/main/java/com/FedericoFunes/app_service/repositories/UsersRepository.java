package com.FedericoFunes.app_service.repositories;

import com.FedericoFunes.app_service.entities.UsersEntity;
import com.FedericoFunes.app_service.entities.enums.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    Optional<UsersEntity> findByUsername(String username);
    Optional<UsersEntity> findByEmail(String email);
    List<UsersEntity> findByRoleIdAndRole(Long roleId, Roles role);
    Optional<UsersEntity> findByRoleId(Long roleId);
}
