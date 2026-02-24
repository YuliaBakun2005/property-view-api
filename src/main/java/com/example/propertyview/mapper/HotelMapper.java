package com.example.propertyview.mapper;

import com.example.propertyview.dto.AddressDto;
import com.example.propertyview.dto.AmenityDto;
import com.example.propertyview.dto.ArrivalTimeDto;
import com.example.propertyview.dto.ContactsDto;
import com.example.propertyview.dto.HotelCreateUpdateDto;
import com.example.propertyview.dto.HotelDetailDto;
import com.example.propertyview.dto.HotelShortDto;
import com.example.propertyview.entity.Address;
import com.example.propertyview.entity.Amenity;
import com.example.propertyview.entity.ArrivalTime;
import com.example.propertyview.entity.Contacts;
import com.example.propertyview.entity.Hotel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface HotelMapper {

    @Mapping(target = "city", source = "address.city")
    @Mapping(target = "country", source = "address.country")
    HotelShortDto toShortDto(Hotel hotel);

    List<HotelShortDto> toShortDtoList(List<Hotel> hotels);

    HotelDetailDto toDetailDto(Hotel hotel);

    AddressDto toDto(Address address);

    Address toEntity(AddressDto dto);

    ContactsDto toDto(Contacts contacts);

    Contacts toEntity(ContactsDto dto);

    ArrivalTimeDto toDto(ArrivalTime arrivalTime);

    ArrivalTime toEntity(ArrivalTimeDto dto);

    AmenityDto toDto(Amenity amenity);

    List<AmenityDto> toAmenityDtoList(Set<Amenity> amenities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "arrivalTime", source = "arrivalTime")
    @Mapping(target = "contacts", source = "contacts")
    @Mapping(target = "address", source = "address")
    Hotel toEntity(HotelCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amenities", ignore = true)
    void updateHotelFromDto(HotelCreateUpdateDto dto, @MappingTarget Hotel hotel);
}

