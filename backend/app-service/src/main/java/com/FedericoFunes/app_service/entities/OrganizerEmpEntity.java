package com.FedericoFunes.app_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "organizer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Maybe a person (a patient what needs blood urgently), maybe a organization (blood bank, cooperative, etc)
    @Column(nullable = false, name = "full_name")
    private String fullName;

    //DNI or CUIT
    @Column(nullable = false)
    private String document;
}
