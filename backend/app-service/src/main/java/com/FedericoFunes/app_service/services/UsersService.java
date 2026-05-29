package com.FedericoFunes.app_service.services;

import com.FedericoFunes.app_service.dtos.users.RequestUsersDTO;
import com.FedericoFunes.app_service.dtos.users.ResponseUsersDTO;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {
    ResponseUsersDTO findByUsername(String username);
    ResponseUsersDTO registerUser(RequestUsersDTO dto);
}
