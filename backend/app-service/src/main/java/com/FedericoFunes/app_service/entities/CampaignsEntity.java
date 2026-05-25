package com.FedericoFunes.app_service.entities;

import com.FedericoFunes.app_service.entities.enums.BloodFactor;
import com.FedericoFunes.app_service.entities.enums.BloodGroup;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "campaigns")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampaignsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    @Column(nullable = false, name = "start_time")
    private LocalTime startTime;

    @Column(nullable = false)
    private String direction;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Organizer creator;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_factor_required")
    private BloodFactor bloodFactorRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "blood_group_required")
    private BloodGroup bloodGroupRequired;

    @ManyToMany
    @JoinTable(
            name = "campaign_donors",
            joinColumns = @JoinColumn(name = "campaign_id"),
            inverseJoinColumns = @JoinColumn(name = "donor_id")
    )
    private List<DonorEntity> subscribedDonors = new ArrayList<>();

    @Column(nullable = false)
    private Boolean isActive;
}
