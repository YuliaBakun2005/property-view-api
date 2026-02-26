package com.example.propertyview.repository;

import com.example.propertyview.entity.Amenity;
import com.example.propertyview.entity.Hotel;
import com.example.propertyview.repository.projection.ValueCountProjection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@org.springframework.test.context.TestPropertySource(properties = "logging.level.org.springframework.test.context.support.AnnotationConfigContextLoaderUtils=WARN")
@Transactional
class HotelRepositoryIntegrationTest {


    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Test
    void countByBrand_returnsCorrectCounts() {
        hotelRepository.saveAll(List.of(
                hotel("Hotel 1", "Hilton"),
                hotel("Hotel 2", "Hilton"),
                hotel("Hotel 3", "Marriott")
        ));

        Map<String, Long> counts = hotelRepository.countByBrand().stream()
                .collect(Collectors.toMap(ValueCountProjection::getValue, ValueCountProjection::getCount));

        assertThat(counts.get("Hilton")).isNotNull().isGreaterThanOrEqualTo(2L);
        assertThat(counts.get("Marriott")).isEqualTo(1L);
    }

    @Test
    void countByAmenity_returnsCorrectCounts() {
        Amenity wifi = amenity("Free WiFi");
        Amenity parking = amenity("Free parking");
        amenityRepository.saveAll(List.of(wifi, parking));

        hotelRepository.saveAll(List.of(
                hotelWithAmenities("Hotel 1", "Brand", Set.of(wifi, parking)),
                hotelWithAmenities("Hotel 2", "Brand", Set.of(wifi))
        ));

        Map<String, Long> counts = hotelRepository.countByAmenity().stream()
                .collect(Collectors.toMap(ValueCountProjection::getValue, ValueCountProjection::getCount));

        assertThat(counts.get("Free WiFi")).isEqualTo(2L);
        assertThat(counts.get("Free parking")).isEqualTo(1L);
    }

    // helpers

    private Hotel hotel(String name, String brand) {
        Hotel h = new Hotel();
        h.setName(name);
        h.setBrand(brand);
        return h;
    }

    private Hotel hotelWithAmenities(String name, String brand, Set<Amenity> amenities) {
        Hotel h = hotel(name, brand);
        h.setAmenities(new HashSet<>(amenities));
        return h;
    }

    private Amenity amenity(String name) {
        Amenity a = new Amenity();
        a.setName(name);
        return a;
    }
}

