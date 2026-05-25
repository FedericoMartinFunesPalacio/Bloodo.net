package com.FedericoFunes.app_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "organizer_emp")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrganizerEmpEntity extends Organizer {

    @Column(nullable = false, name = "full_name")
    private String fullName;

}
