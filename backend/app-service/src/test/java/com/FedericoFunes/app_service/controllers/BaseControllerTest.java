package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.repositories.UsersRepository;
import com.FedericoFunes.app_service.security.JwtUtil;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class BaseControllerTest {

    @MockBean
    protected JwtUtil jwtUtil;

    @MockBean
    protected UsersRepository usersRepository;
}
