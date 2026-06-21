package com.FedericoFunes.app_service.dtos.donor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BloodTypePercentageDTO {

    @JsonProperty("blood_type")
    private String bloodType;

    @JsonProperty("count")
    private Long count;

    @JsonProperty("percentage")
    private Double percentage;
}
