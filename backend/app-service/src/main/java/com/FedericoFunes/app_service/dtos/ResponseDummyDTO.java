package com.FedericoFunes.app_service.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDummyDTO {
    private Long id;

    private String name;

    private String description;

    @JsonProperty("serial_number")
    private String serialNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonProperty("created_at")
    private LocalDate createdAt;
}
