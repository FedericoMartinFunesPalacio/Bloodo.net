package com.FedericoFunes.app_service.dtos.campaigns;

import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestCampaignsDTO {
    @NotBlank
    @NotNull
    private String title;

    @NotBlank
    @NotNull
    private String description;

    @JsonProperty("start_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull
    private LocalDate startDate;

    @JsonProperty("end_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @JsonProperty("start_time")
    @NotNull
    private LocalTime startTime;

    @NotBlank
    @NotNull
    private String direction;

    @JsonProperty("blood_factor_required")
    private BloodFactor bloodFactorRequired;

    @JsonProperty("blood_group_required")
    private BloodGroup bloodGroupRequired;

    @JsonProperty("organizer_id")
    @NotNull
    private Long organizerId;
}

