package com.example.propertyview.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.example.propertyview.dto.common.AddressDto;
import com.example.propertyview.dto.common.ContactDto;
import com.example.propertyview.dto.common.ArrivalTimeDto;

import java.util.List;

public class HotelCreateUpdateDto {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String brand;

    @NotNull
    private AddressDto address;

    private ContactDto contacts;

    private ArrivalTimeDto arrivalTime;

    private List<String> amenityNames;

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

    public List<String> getAmenityNames() {
        return amenityNames;
    }

    public void setAmenityNames(List<String> amenityNames) {
        this.amenityNames = amenityNames;
    }
}

