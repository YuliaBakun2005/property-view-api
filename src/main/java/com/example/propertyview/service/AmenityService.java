package com.example.propertyview.service;

import com.example.propertyview.entity.Amenity;
import com.example.propertyview.entity.Hotel;

import java.util.List;
import java.util.Set;

public interface AmenityService {
    Set<Amenity> resolveAmenities(List<String> names);
    void applyAmenitiesFromNames(List<String> amenityNames, Hotel hotel);
    void mergeAmenities(Hotel hotel, List<String> amenityNames);
}
