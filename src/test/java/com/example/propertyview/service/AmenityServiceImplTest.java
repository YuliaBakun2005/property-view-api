package com.example.propertyview.service;

import com.example.propertyview.entity.Amenity;
import com.example.propertyview.repository.AmenityRepository;
import com.example.propertyview.entity.Hotel;
import com.example.propertyview.service.impl.AmenityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class AmenityServiceImplTest {

    @Mock
    private AmenityRepository amenityRepository;

    private AmenityServiceImpl service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        service = new AmenityServiceImpl(amenityRepository);
    }

    @Test
    void resolveAmenities_returnsEmpty_forNullOrEmpty() {
        assertThat(service.resolveAmenities(null)).isEmpty();
        assertThat(service.resolveAmenities(List.of())).isEmpty();
    }

    @Test
    void resolveAmenities_usesRepository_andAddsMissing() {
        // repository has one amenity 'wifi'
        Amenity wifi = new Amenity();
        wifi.setId(1L);
        wifi.setName("WiFi");
        when(amenityRepository.findByNameIgnoreCaseIn(List.of("wifi","pool")))
                .thenReturn(List.of(wifi));

        Set<Amenity> result = service.resolveAmenities(List.of("wifi", "pool"));
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Amenity::getName)
                .map(String::toLowerCase)
                .containsExactlyInAnyOrder("wifi", "pool");
    }

    @Test
    void applyAmenitiesFromNames_setsHotelAmenities() {
        Hotel hotel = new Hotel();
        service.applyAmenitiesFromNames(List.of("a","b"), hotel);
        assertThat(hotel.getAmenities()).hasSize(2);
    }

    @Test
    void mergeAmenities_combinesExistingAndNew() {
        Hotel hotel = new Hotel();
        Amenity existing = new Amenity();
        existing.setName("x");
        hotel.setAmenities(new HashSet<>(Set.of(existing)));

        service.mergeAmenities(hotel, List.of("x","y"));
        assertThat(hotel.getAmenities()).extracting(Amenity::getName)
                .map(String::toLowerCase)
                .contains("x","y");
    }
}
