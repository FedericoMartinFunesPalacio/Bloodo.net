package com.FedericoFunes.app_service.dtos.donor;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonorHealthDTO {

    @JsonProperty("blood_type")
    private String bloodType;

    @JsonProperty("last_donation_date")
    private String lastDonationDate;

    @JsonProperty("next_eligible_date")
    private String nextEligibleDate;

    @JsonProperty("bmi")
    private Double bmi;

    @JsonProperty("age")
    private Integer age;
}
