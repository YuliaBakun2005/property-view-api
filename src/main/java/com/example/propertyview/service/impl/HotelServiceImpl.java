package com.example.propertyview.service.impl;

import com.example.propertyview.dto.create.AddAmenitiesRequestDto;
import com.example.propertyview.dto.create.HotelCreateUpdateDto;
import com.example.propertyview.dto.read.HotelDetailDto;
import com.example.propertyview.dto.read.HotelShortDto;
import com.example.propertyview.entity.Hotel;
import com.example.propertyview.exception.BadRequestException;
import com.example.propertyview.exception.NotFoundException;
import com.example.propertyview.mapper.HotelMapper;
import com.example.propertyview.repository.HotelRepository;
import com.example.propertyview.service.AmenityService;
import com.example.propertyview.service.HistogramParam;
import com.example.propertyview.service.HotelService;
import com.example.propertyview.specification.HotelSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityService amenityService;
    private final HotelMapper hotelMapper;

    public HotelServiceImpl(HotelRepository hotelRepository,
                            AmenityService amenityService,
                            HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.amenityService = amenityService;
        this.hotelMapper = hotelMapper;
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<HotelShortDto> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAll();
        return hotelMapper.toShortDtoList(hotels);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public HotelDetailDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Hotel not found with id: " + id));
        return hotelMapper.toDetailDto(hotel);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<HotelShortDto> searchHotels(String name,
                                            String brand,
                                            String city,
                                            String country,
                                            List<String> amenities) {
        Specification<Hotel> spec = Specification.where(HotelSpecifications.nameLike(name))
                .and(HotelSpecifications.brandLike(brand))
                .and(HotelSpecifications.cityLike(city))
                .and(HotelSpecifications.countryLike(country))
                .and(HotelSpecifications.amenitiesIn(amenities));

        List<Hotel> result = hotelRepository.findAll(spec);
        return hotelMapper.toShortDtoList(result);
    }

    @Override
    public HotelDetailDto createHotel(HotelCreateUpdateDto dto) {
        Hotel hotel = hotelMapper.toEntity(dto);
        amenityService.applyAmenitiesFromNames(dto.getAmenityNames(), hotel);
        Hotel saved = hotelRepository.save(hotel);
        return hotelMapper.toDetailDto(saved);
    }

    @Override
    public HotelDetailDto addAmenities(Long hotelId, AddAmenitiesRequestDto request) {
        if (request == null || CollectionUtils.isEmpty(request.getAmenityNames())) {
            throw new BadRequestException("Amenity names must be provided");
        }

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new NotFoundException("Hotel not found with id: " + hotelId));

        amenityService.mergeAmenities(hotel, request.getAmenityNames());
        Hotel saved = hotelRepository.save(hotel);
        return hotelMapper.toDetailDto(saved);
    }

    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public Map<String, Long> getHistogram(HistogramParam param) {
        Map<String, Long> result = new HashMap<>();

        switch (param) {
            case BRAND -> hotelRepository.countByBrand()
                    .forEach(vc -> result.put(vc.getValue(), vc.getCount()));
            case CITY -> hotelRepository.countByCity()
                    .forEach(vc -> result.put(vc.getValue(), vc.getCount()));
            case COUNTRY -> hotelRepository.countByCountry()
                    .forEach(vc -> result.put(vc.getValue(), vc.getCount()));
            case AMENITIES -> hotelRepository.countByAmenity()
                    .forEach(vc -> result.put(vc.getValue(), vc.getCount()));
            default -> throw new BadRequestException("Unsupported histogram parameter: " + param.getValue());
        }

        return result;
    }

    // helper logic has been moved to dedicated components (AmenityService and HotelSpecifications)
}

