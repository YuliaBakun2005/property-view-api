package com.example.propertyview.service.impl;

import com.example.propertyview.dto.AddAmenitiesRequestDto;
import com.example.propertyview.dto.HotelCreateUpdateDto;
import com.example.propertyview.dto.HotelDetailDto;
import com.example.propertyview.dto.HotelShortDto;
import com.example.propertyview.entity.Amenity;
import com.example.propertyview.entity.Hotel;
import com.example.propertyview.exception.BadRequestException;
import com.example.propertyview.exception.NotFoundException;
import com.example.propertyview.mapper.HotelMapper;
import com.example.propertyview.repository.AmenityRepository;
import com.example.propertyview.repository.HotelRepository;
import com.example.propertyview.service.HistogramParam;
import com.example.propertyview.service.HotelService;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    public HotelServiceImpl(HotelRepository hotelRepository,
                            AmenityRepository amenityRepository,
                            HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.amenityRepository = amenityRepository;
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
        Specification<Hotel> spec = Specification.where(null);

        if (StringUtils.hasText(name)) {
            String namePattern = "%" + name.toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("name")), namePattern));
        }

        if (StringUtils.hasText(brand)) {
            String brandPattern = "%" + brand.toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("brand")), brandPattern));
        }

        if (StringUtils.hasText(city)) {
            String cityPattern = "%" + city.toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("address").get("city")), cityPattern));
        }

        if (StringUtils.hasText(country)) {
            String countryPattern = "%" + country.toLowerCase(Locale.ROOT) + "%";
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("address").get("country")), countryPattern));
        }

        if (!CollectionUtils.isEmpty(amenities)) {
            List<String> lowered = amenities.stream()
                    .filter(StringUtils::hasText)
                    .map(a -> a.toLowerCase(Locale.ROOT))
                    .toList();

            if (!lowered.isEmpty()) {
                spec = spec.and((root, query, cb) -> {
                    query.distinct(true);
                    var join = root.join("amenities");
                    return cb.lower(join.get("name")).in(lowered);
                });
            }
        }

        List<Hotel> result = hotelRepository.findAll(spec);
        return hotelMapper.toShortDtoList(result);
    }

    @Override
    public HotelDetailDto createHotel(HotelCreateUpdateDto dto) {
        Hotel hotel = hotelMapper.toEntity(dto);

        if (!CollectionUtils.isEmpty(dto.getAmenityNames())) {
            Set<Amenity> amenities = resolveAmenities(dto.getAmenityNames());
            hotel.setAmenities(amenities);
        }

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

        Set<Amenity> existing = Optional.ofNullable(hotel.getAmenities())
                .map(HashSet::new)
                .orElseGet(HashSet::new);

        Set<Amenity> toAdd = resolveAmenities(request.getAmenityNames());
        existing.addAll(toAdd);
        hotel.setAmenities(existing);

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

    private Set<Amenity> resolveAmenities(List<String> names) {
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

        List<Amenity> existing = amenityRepository.findAll().stream()
                .filter(a -> normalized.contains(a.getName().toLowerCase(Locale.ROOT)))
                .toList();

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
}

