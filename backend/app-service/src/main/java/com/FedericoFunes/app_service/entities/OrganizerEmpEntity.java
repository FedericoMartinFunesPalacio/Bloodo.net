package com.FedericoFunes.app_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "organizer_empress")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerEmpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "full_name")
    private String fullName;

    //CUIT
    @Column(nullable = false)
    private String document;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;
}
