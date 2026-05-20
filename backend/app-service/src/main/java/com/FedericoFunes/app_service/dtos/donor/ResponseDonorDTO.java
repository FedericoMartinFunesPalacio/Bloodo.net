package com.FedericoFunes.app_service.dtos.donor;

import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
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
public class ResponseDonorDTO {
    @JsonProperty("first_name")
    @NotBlank
    @NotNull
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank
    @NotNull
    private String lastName;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotBlank
    @NotNull
    private LocalDate birthdate;

    @NotBlank
    @NotNull
    private String document;

    @JsonProperty("blood_factor")
    @NotBlank
    @NotNull
    private BloodFactor bloodFactor; //ENUM

    @JsonProperty("blood_group")
    @NotBlank
    @NotNull
    private BloodGroup bloodGroup; //ENUM

    @NotBlank
    @NotNull
    private Gender gender; //ENUM

    @NotBlank
    @NotNull
    private Double height; //IN METERS

    @NotBlank
    @NotNull
    private Double weight; //IN KILOGRAMS

    @NotBlank
    @NotNull
    @Email
    private String email;

    @JsonProperty("phone_number")
    @NotBlank
    @NotNull
    private String phoneNumber;

    @JsonProperty("is_active")
    @NotBlank
    @NotNull
    private Boolean isActive;
}
