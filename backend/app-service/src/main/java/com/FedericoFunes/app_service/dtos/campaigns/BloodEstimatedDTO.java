package com.FedericoFunes.app_service.dtos.campaigns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodEstimatedDTO {

    @JsonProperty("campaign_id")
    private Long campaignId;

    @JsonProperty("campaign_title")
    private String campaignTitle;

    @JsonProperty("status")
    private String status;

    @JsonProperty("subscribed_donors")
    private Long subscribedDonors;

    @JsonProperty("estimated_ml")
    private Double estimatedMl;

    @JsonProperty("estimated_liters")
    private Double estimatedLiters;
}
