package com.sporton.SportOn.repository;

import com.sporton.SportOn.entity.Venue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    Optional<Venue> findByName(String name);

    Page<Venue> findByProviderId(Long id, PageRequest pageRequest);

    @Query("SELECT v FROM Venue v WHERE " +
            "6371 * acos(cos(radians(:userLatitude)) * cos(radians(v.latitude)) * cos(radians(v.longitude) - radians(:userLongitude)) + " +
            "sin(radians(:userLatitude)) * sin(radians(v.latitude))) <= 5")
    List<Venue> findVenuesNearUser(@Param("userLatitude") double userLatitude,
                                   @Param("userLongitude") double userLongitude, PageRequest pageRequest);
    @Query("SELECT v FROM Venue v ORDER BY (SELECT AVG(r.rating) FROM Rating r WHERE r.venue = v) DESC")
    List<Venue> findPopularVenues(PageRequest pageRequest);

    List<Venue> findByNameContainingIgnoreCase(String name);
}
