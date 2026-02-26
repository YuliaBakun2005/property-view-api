package com.example.propertyview.service;

import com.example.propertyview.dto.create.AddAmenitiesRequestDto;
import com.example.propertyview.dto.create.HotelCreateUpdateDto;
import com.example.propertyview.dto.read.HotelDetailDto;
import com.example.propertyview.dto.read.HotelShortDto;

import java.util.List;
import java.util.Map;

public interface HotelService {

    List<HotelShortDto> getAllHotels();

    HotelDetailDto getHotelById(Long id);

    List<HotelShortDto> searchHotels(String name,
                                     String brand,
                                     String city,
                                     String country,
                                     List<String> amenities);

    HotelDetailDto createHotel(HotelCreateUpdateDto dto);

    HotelDetailDto addAmenities(Long hotelId, AddAmenitiesRequestDto request);

    Map<String, Long> getHistogram(HistogramParam param);
}

