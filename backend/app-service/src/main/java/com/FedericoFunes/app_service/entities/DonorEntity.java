package com.FedericoFunes.app_service.entities;

import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import com.FedericoFunes.app_service.entities.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "donor")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DonorEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "blood_factor")
    private BloodFactor bloodFactor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "blood_group")
    private BloodGroup bloodGroup;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private Double height; //IN METERS

    @Column(nullable = false)
    private Double weight; //IN KILOGRAMS

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;
}
