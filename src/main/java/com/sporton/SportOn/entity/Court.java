package com.sporton.SportOn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Court {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long venueId;
    @NonNull
    private String name;
    @NonNull
    private CourtSurface surface;
    @NonNull
    private Double width;
    @NonNull
    private Double height;
    @NonNull
    private Integer activePlayersPerTeam;
    @NonNull
    private Double basePrice;
    private String additionalInfo;
}
