package com.sporton.SportOn.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regionId;
    @NonNull
    @Column(unique = true)
    private String name;
    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Venue> venues;
}
