package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.security.JwtUtil;
import com.FedericoFunes.app_service.services.UsersService;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class BaseControllerTest {

    @MockBean
    protected JwtUtil jwtUtil;

    @MockBean
    protected UsersService usersService;
}
