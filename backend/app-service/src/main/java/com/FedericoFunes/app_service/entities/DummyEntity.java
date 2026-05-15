package com.FedericoFunes.app_service.entities;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import lombok.*;

import java.time.LocalDate;

@Entity(name = "dummy")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DummyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, name = "serial_number")
    private String serialNumber;

    @Column(nullable = false, name = "created_at")
    private LocalDate createdAt;
}
