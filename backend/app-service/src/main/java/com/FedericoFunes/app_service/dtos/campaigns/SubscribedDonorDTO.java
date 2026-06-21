package com.FedericoFunes.app_service.dtos.campaigns;

import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribedDonorDTO {
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;

    private String document;

    @JsonProperty("blood_group")
    private BloodGroup bloodGroup;

    @JsonProperty("blood_factor")
    private BloodFactor bloodFactor;

    @JsonProperty("is_active")
    private Boolean isActive;
}

