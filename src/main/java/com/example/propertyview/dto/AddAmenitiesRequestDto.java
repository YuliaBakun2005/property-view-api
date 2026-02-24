package com.example.propertyview.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class AddAmenitiesRequestDto {

    @NotEmpty
    private List<String> amenityNames;

    public List<String> getAmenityNames() {
        return amenityNames;
    }

    public void setAmenityNames(List<String> amenityNames) {
        this.amenityNames = amenityNames;
    }
}

