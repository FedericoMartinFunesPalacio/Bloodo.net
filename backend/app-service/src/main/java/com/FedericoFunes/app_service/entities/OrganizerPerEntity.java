package com.FedericoFunes.app_service.entities;

import com.FedericoFunes.app_service.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "organizer_per")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerPerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false, name = "birthdate")
    private LocalDate birthdate;

    @Column(nullable = false)
    private String document;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(name = "is_active")
    private Boolean isActive;
}
