package com.example.propertyview.controller;

import com.example.propertyview.dto.AddAmenitiesRequestDto;
import com.example.propertyview.dto.HotelCreateUpdateDto;
import com.example.propertyview.dto.HotelDetailDto;
import com.example.propertyview.dto.HotelShortDto;
import com.example.propertyview.service.HistogramParam;
import com.example.propertyview.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "Hotels", description = "Hotel operations")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/hotels")
    @Operation(summary = "Get all hotels", description = "Returns short information about all hotels")
    public List<HotelShortDto> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @GetMapping("/hotels/{id}")
    @Operation(summary = "Get hotel by id", description = "Returns full hotel details")
    public HotelDetailDto getHotelById(@PathVariable Long id) {
        return hotelService.getHotelById(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Search hotels", description = "Search hotels by name, brand, city, country, amenities")
    public List<HotelShortDto> searchHotels(@RequestParam(name = "name", required = false) String name,
                                            @RequestParam(name = "brand", required = false) String brand,
                                            @RequestParam(name = "city", required = false) String city,
                                            @RequestParam(name = "country", required = false) String country,
                                            @RequestParam(name = "amenities", required = false) List<String> amenities) {
        return hotelService.searchHotels(name, brand, city, country, amenities);
    }

    @PostMapping("/hotels")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create hotel", description = "Creates a new hotel")
    public HotelDetailDto createHotel(@Valid @RequestBody HotelCreateUpdateDto dto) {
        return hotelService.createHotel(dto);
    }

    @PostMapping("/hotels/{id}/amenities")
    @Operation(summary = "Add amenities to hotel", description = "Adds amenities to an existing hotel")
    public HotelDetailDto addAmenities(@PathVariable Long id,
                                       @Valid @RequestBody AddAmenitiesRequestDto request) {
        return hotelService.addAmenities(id, request);
    }

    @GetMapping("/histogram/{param}")
    @Operation(summary = "Get histogram", description = "Returns histogram by brand, city, country or amenities")
    public Map<String, Long> getHistogram(@PathVariable String param) {
        HistogramParam histogramParam = HistogramParam.from(param);
        return hotelService.getHistogram(histogramParam);
    }
}

