package com.example.propertyview.service.impl;

import com.example.propertyview.entity.Amenity;
import com.example.propertyview.entity.Hotel;
import com.example.propertyview.repository.AmenityRepository;
import com.example.propertyview.service.AmenityService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;

    public AmenityServiceImpl(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    @Override
    public Set<Amenity> resolveAmenities(List<String> names) {
        if (CollectionUtils.isEmpty(names)) {
            return new HashSet<>();
        }

        Set<String> normalized = names.stream()
                .filter(StringUtils::hasText)
                .map(name -> name.trim().toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        if (normalized.isEmpty()) {
            return new HashSet<>();
        }

        // query only matching amenities rather than loading everything
        List<Amenity> existing = amenityRepository.findByNameIgnoreCaseIn(normalized);

        Map<String, Amenity> byName = existing.stream()
                .collect(Collectors.toMap(a -> a.getName().toLowerCase(Locale.ROOT), a -> a));

        Set<Amenity> result = new HashSet<>(existing);

        for (String name : normalized) {
            if (!byName.containsKey(name)) {
                Amenity amenity = new Amenity();
                amenity.setName(name);
                result.add(amenity);
            }
        }

        return result;
    }

    @Override
    public void applyAmenitiesFromNames(List<String> amenityNames, Hotel hotel) {
        if (CollectionUtils.isEmpty(amenityNames)) {
            return;
        }
        Set<Amenity> amenities = resolveAmenities(amenityNames);
        hotel.setAmenities(amenities);
    }

    @Override
    public void mergeAmenities(Hotel hotel, List<String> amenityNames) {
        Set<Amenity> existing = Optional.ofNullable(hotel.getAmenities())
                .map(HashSet::new)
                .orElseGet(HashSet::new);

        Set<Amenity> toAdd = resolveAmenities(amenityNames);
        existing.addAll(toAdd);
        hotel.setAmenities(existing);
    }
}
