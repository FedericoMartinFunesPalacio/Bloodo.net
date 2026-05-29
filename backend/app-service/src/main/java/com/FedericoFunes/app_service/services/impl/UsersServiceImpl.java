package com.FedericoFunes.app_service.services.impl;

import com.FedericoFunes.app_service.dtos.users.RequestUsersDTO;
import com.FedericoFunes.app_service.dtos.users.ResponseUsersDTO;
import com.FedericoFunes.app_service.entities.UsersEntity;
import com.FedericoFunes.app_service.repositories.UsersRepository;
import com.FedericoFunes.app_service.services.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository usersRepository;

    private ResponseUsersDTO EntityToDTO(UsersEntity usersEntity) {
        try {
            ResponseUsersDTO dto = new ResponseUsersDTO();
            dto.setUsername(usersEntity.getUsername());
            dto.setEmail(usersEntity.getEmail());
            dto.setPhone(usersEntity.getPhone());
            dto.setRole(usersEntity.getRole());
            return dto;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(500),"Error mapping usersDTO: " + e.getMessage());
        }
    }

    @Override
    public ResponseUsersDTO findByUsername(String username) {
        UsersEntity entity = usersRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        return EntityToDTO(entity);
    }

    @Override
    public ResponseUsersDTO registerUser(RequestUsersDTO dto) {
        UsersEntity entity = new UsersEntity();
        entity.setUsername(dto.getUsername());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        entity.setRole(dto.getRole());

        UsersEntity savedEntity = usersRepository.save(entity);
        return EntityToDTO(savedEntity);
    }
}
