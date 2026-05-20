package com.FedericoFunes.app_service.handlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionResponseDTO {
    private int code;
    private String description;
    private LocalDateTime timestamp;
}

