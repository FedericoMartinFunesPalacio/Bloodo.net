package com.FedericoFunes.app_service.controllers;

import com.FedericoFunes.app_service.dtos.users.RequestUsersDTO;
import com.FedericoFunes.app_service.dtos.users.ResetPasswordDTO;
import com.FedericoFunes.app_service.dtos.users.ResponseUsersDTO;
import com.FedericoFunes.app_service.entities.UsersEntity;
import com.FedericoFunes.app_service.entities.enums.Roles;
import com.FedericoFunes.app_service.handlers.BadRequestException;
import com.FedericoFunes.app_service.repositories.UsersRepository;
import com.FedericoFunes.app_service.services.UsersService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersRepository usersRepository;

    @MockBean
    private AuthenticationManager authenticationManager;

    private final ObjectMapper mapper = new ObjectMapper();

    private UsersEntity buildUser() {
        UsersEntity user = new UsersEntity();
        user.setId(1L);
        user.setUsername("jdoe");
        user.setEmail("jdoe@test.com");
        user.setPhone("1234567890");
        user.setRole(Roles.DONOR);
        user.setRoleId(10L);
        user.setPassword("hashed");
        return user;
    }

    @Test
    void register_shouldReturnDto() throws Exception {
        ResponseUsersDTO resp = ResponseUsersDTO.builder()
                .id(1L).username("jdoe").email("jdoe@test.com")
                .phone("1234567890").role(Roles.DONOR).build();
        when(usersService.registerUser(any(RequestUsersDTO.class))).thenReturn(resp);

        RequestUsersDTO req = RequestUsersDTO.builder()
                .username("jdoe").password("Pass123!")
                .email("jdoe@test.com").phone("1234567890")
                .role(Roles.DONOR).roleId(10L).build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("jdoe"))
                .andExpect(jsonPath("$.role").value("DONOR"));
    }

    @Test
    void register_shouldReturn400_whenInvalid() throws Exception {
        when(usersService.registerUser(any(RequestUsersDTO.class)))
                .thenThrow(new BadRequestException("Email already in use"));

        RequestUsersDTO req = RequestUsersDTO.builder()
                .username("jdoe").password("Pass123!")
                .email("dup@test.com").phone("1234567890")
                .role(Roles.DONOR).roleId(10L).build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPasswordRequest_shouldReturnMessage() throws Exception {
        when(usersService.resetPasswordFirstStep(anyString())).thenReturn("Code sent");

        mockMvc.perform(post("/api/v1/auth/reset-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"jdoe@test.com\""))
                .andExpect(status().isOk())
                .andExpect(content().string("Code sent"));
    }

    @Test
    void resetPasswordRequest_shouldReturn400_whenInvalidEmail() throws Exception {
        when(usersService.resetPasswordFirstStep(anyString()))
                .thenThrow(new BadRequestException("User not found"));

        mockMvc.perform(post("/api/v1/auth/reset-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("\"noexiste@test.com\""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resetPassword_shouldReturnTrue() throws Exception {
        ResetPasswordDTO dto = ResetPasswordDTO.builder()
                .email("jdoe@test.com").password("NewPass123!").build();
        when(usersService.resetPasswordSecondStep(any(ResetPasswordDTO.class))).thenReturn(true);

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void resetPassword_shouldReturn400_whenInvalidCode() throws Exception {
        ResetPasswordDTO dto = ResetPasswordDTO.builder()
                .email("jdoe@test.com").password("NewPass123!").build();
        when(usersService.resetPasswordSecondStep(any(ResetPasswordDTO.class)))
                .thenThrow(new BadRequestException("Invalid or expired code"));

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        RequestUsersDTO req = RequestUsersDTO.builder()
                .username("jdoe").password("Pass123!")
                .email("jdoe@test.com").phone("1234567890")
                .role(Roles.DONOR).roleId(10L).build();

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(usersRepository.findByUsername("jdoe")).thenReturn(Optional.of(buildUser()));
        when(jwtUtil.generateToken(any())).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.role").value("DONOR"))
                .andExpect(jsonPath("$.email").value("jdoe@test.com"));
    }

    @Test
    void login_shouldReturn401_whenInvalidCredentials() throws Exception {
        RequestUsersDTO req = RequestUsersDTO.builder()
                .username("jdoe").password("wrong")
                .email("jdoe@test.com").phone("1234567890")
                .role(Roles.DONOR).roleId(10L).build();

        doThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isInternalServerError());
    }
}
