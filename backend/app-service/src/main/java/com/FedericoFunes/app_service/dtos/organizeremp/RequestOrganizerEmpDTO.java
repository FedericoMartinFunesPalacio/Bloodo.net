package com.FedericoFunes.app_service.dtos.organizeremp;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class RequestOrganizerEmpDTO {
    @JsonProperty("full_name")
    @NotBlank
    @NotNull
    private String fullName;

    @NotBlank
    @NotNull
    private String document;

    @NotBlank
    @NotNull
    private String direction;

    @NotBlank
    @NotNull
    @Email
    private String email;

    @JsonProperty("phone_number")
    @NotBlank
    @NotNull
    private String phoneNumber;
}

