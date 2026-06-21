package com.FedericoFunes.app_service.dtos.donor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonorStatsDTO {

    @JsonProperty("campaigns_attended")
    private Long campaignsAttended;

    @JsonProperty("estimated_ml")
    private Double estimatedMl;

    @JsonProperty("estimated_liters")
    private Double estimatedLiters;
}
