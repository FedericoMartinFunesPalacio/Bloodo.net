package com.FedericoFunes.app_service.dtos.campaigns;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TotalLivesSavedDTO {

    @JsonProperty("total_subscribers")
    private Long totalSubscribers;

    @JsonProperty("total_finished_campaigns")
    private Long totalFinishedCampaigns;

    @JsonProperty("estimated_lives_saved")
    private Long estimatedLivesSaved;
}
