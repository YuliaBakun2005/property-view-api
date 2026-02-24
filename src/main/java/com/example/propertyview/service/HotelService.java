package com.example.propertyview.service;

import com.example.propertyview.dto.AddAmenitiesRequestDto;
import com.example.propertyview.dto.HotelCreateUpdateDto;
import com.example.propertyview.dto.HotelDetailDto;
import com.example.propertyview.dto.HotelShortDto;

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

