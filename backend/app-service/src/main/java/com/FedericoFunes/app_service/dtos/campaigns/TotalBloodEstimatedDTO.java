package com.FedericoFunes.app_service.dtos.campaigns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalBloodEstimatedDTO {

    @JsonProperty("total_subscribers")
    private Long totalSubscribers;

    @JsonProperty("total_campaigns")
    private Long totalCampaigns;

    @JsonProperty("estimated_ml")
    private Double estimatedMl;

    @JsonProperty("estimated_liters")
    private Double estimatedLiters;
}
