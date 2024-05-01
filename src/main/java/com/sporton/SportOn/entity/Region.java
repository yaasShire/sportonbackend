package com.sporton.SportOn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regionId;
    @NonNull
    private String name;
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private List<Venue> venues;
}
