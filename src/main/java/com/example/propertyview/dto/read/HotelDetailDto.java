package com.example.propertyview.dto.read;

import com.example.propertyview.dto.common.AddressDto;
import com.example.propertyview.dto.common.ContactDto;
import com.example.propertyview.dto.common.ArrivalTimeDto;
import com.example.propertyview.dto.common.AmenityDto;
import java.util.List;

public class HotelDetailDto {

    private Long id;
    private String name;
    private String description;
    private String brand;
    private AddressDto address;
    private ContactDto contacts;
    private ArrivalTimeDto arrivalTime;
    private List<AmenityDto> amenities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public AddressDto getAddress() {
        return address;
    }

    public void setAddress(AddressDto address) {
        this.address = address;
    }

    public ContactDto getContacts() {
        return contacts;
    }

    public void setContacts(ContactDto contacts) {
        this.contacts = contacts;
    }

    public ArrivalTimeDto getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(ArrivalTimeDto arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public List<AmenityDto> getAmenities() {
        return amenities;
    }

    public void setAmenities(List<AmenityDto> amenities) {
        this.amenities = amenities;
    }
}

