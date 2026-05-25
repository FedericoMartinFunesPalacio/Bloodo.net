package com.FedericoFunes.app_service.dtos.campaigns;

import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseCampaignsDTO {
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("start_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate startDate;

    @JsonProperty("end_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate endDate;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("blood_factor_required")
    private BloodFactor bloodFactorRequired;

    @JsonProperty("blood_group_required")
    private BloodGroup bloodGroupRequired;

    @JsonProperty("organizer_id")
    private Long organizerId;
}

