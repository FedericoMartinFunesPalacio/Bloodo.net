package com.FedericoFunes.app_service.dtos.organizerper;

import com.FedericoFunes.app_service.entities.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseOrganizerPerDTO {
    @NotBlank
    @NotNull
    private Long id;

    @JsonProperty("first_name")
    @NotBlank
    @NotNull
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank
    @NotNull
    private String lastName;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private LocalDate birthdate;

    @NotBlank
    @NotNull
    private String document;

    @NotBlank
    @NotNull
    private String direction;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @NotNull
    private Gender gender;

    @NotBlank
    @NotNull
    @Email
    private String email;

    @JsonProperty("phone_number")
    @NotBlank
    @NotNull
    private String phoneNumber;

    @JsonProperty("is_active")
    private Boolean isActive;
}

