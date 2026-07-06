package com.FedericoFunes.app_service.dtos.campaigns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeographicDistributionDTO {

    @JsonProperty("campaign_id")
    private Long campaignId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("direction")
    private String direction;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("is_finished")
    private Boolean isFinished;
}
