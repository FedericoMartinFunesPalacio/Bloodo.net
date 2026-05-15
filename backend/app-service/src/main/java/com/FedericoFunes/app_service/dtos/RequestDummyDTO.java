package com.FedericoFunes.app_service.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestDummyDTO {
    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String description;

    @NotBlank
    @NotNull
    @JsonProperty("serial_number")
    private String serialNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @JsonProperty("created_at")
    private LocalDate createdAt;
}
