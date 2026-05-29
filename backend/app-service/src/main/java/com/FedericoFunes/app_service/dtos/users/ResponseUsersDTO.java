package com.FedericoFunes.app_service.dtos.users;

import com.FedericoFunes.app_service.entities.enums.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseUsersDTO {
    @NotBlank
    @NotNull
    private String username;

    @NotBlank
    @NotNull
    @Email
    private String email;

    @NotBlank
    @NotNull
    private String phone;

    @NotBlank
    @NotNull
    private Roles role;
}
