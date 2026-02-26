package com.example.propertyview.repository;

import com.example.propertyview.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    Optional<Amenity> findByName(String name);

    List<Amenity> findByNameIn(List<String> names);

    // case-insensitive search for use in service to avoid loading all amenities
    List<Amenity> findByNameIgnoreCaseIn(Collection<String> names);
}

