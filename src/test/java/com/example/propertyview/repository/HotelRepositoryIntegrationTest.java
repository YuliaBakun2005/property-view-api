package com.example.propertyview.repository;

import com.example.propertyview.entity.Amenity;
import com.example.propertyview.entity.Hotel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class HotelRepositoryIntegrationTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Test
    void countByBrand_returnsCorrectCounts() {
        Hotel h1 = new Hotel();
        h1.setName("Hotel 1");
        h1.setBrand("Hilton");

        Hotel h2 = new Hotel();
        h2.setName("Hotel 2");
        h2.setBrand("Hilton");

        Hotel h3 = new Hotel();
        h3.setName("Hotel 3");
        h3.setBrand("Marriott");

        hotelRepository.saveAll(List.of(h1, h2, h3));

        Map<String, Long> counts = hotelRepository.countByBrand().stream()
                .collect(Collectors.toMap(HotelRepository.ValueCount::getValue, HotelRepository.ValueCount::getCount));

        Long hiltonCount = counts.get("Hilton");
        Long marriottCount = counts.get("Marriott");

        // В общей БД могут уже быть Hilton из других интеграционных тестов,
        // поэтому проверяем, что после добавления как минимум два отеля Hilton существуют
        assertThat(hiltonCount).isNotNull();
        assertThat(hiltonCount).isGreaterThanOrEqualTo(2L);
        assertThat(marriottCount).isEqualTo(1L);
    }

    @Test
    void countByAmenity_returnsCorrectCounts() {
        Amenity wifi = new Amenity();
        wifi.setName("Free WiFi");
        Amenity parking = new Amenity();
        parking.setName("Free parking");

        amenityRepository.saveAll(List.of(wifi, parking));

        Hotel h1 = new Hotel();
        h1.setName("Hotel 1");
        h1.setBrand("Brand");
        h1.setAmenities(new HashSet<>(List.of(wifi, parking)));

        Hotel h2 = new Hotel();
        h2.setName("Hotel 2");
        h2.setBrand("Brand");
        h2.setAmenities(new HashSet<>(List.of(wifi)));

        hotelRepository.saveAll(List.of(h1, h2));

        Map<String, Long> counts = hotelRepository.countByAmenity().stream()
                .collect(Collectors.toMap(HotelRepository.ValueCount::getValue, HotelRepository.ValueCount::getCount));

        assertThat(counts.get("Free WiFi")).isEqualTo(2L);
        assertThat(counts.get("Free parking")).isEqualTo(1L);
    }
}

