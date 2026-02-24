package com.example.propertyview.api;

import com.example.propertyview.dto.AddressDto;
import com.example.propertyview.dto.ArrivalTimeDto;
import com.example.propertyview.dto.ContactsDto;
import com.example.propertyview.dto.HotelCreateUpdateDto;
import com.example.propertyview.dto.HotelDetailDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HotelApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + "/property-view" + path;
    }

    @Test
    void createHotel_thenGetByIdAndHistogramCity() {
        HotelCreateUpdateDto createDto = new HotelCreateUpdateDto();
        createDto.setName("DoubleTree by Hilton Minsk");
        createDto.setBrand("Hilton");

        AddressDto address = new AddressDto();
        address.setHouseNumber("9");
        address.setStreet("Pobediteley Avenue");
        address.setCity("Minsk");
        address.setCountry("Belarus");
        address.setPostCode("220004");
        createDto.setAddress(address);

        ContactsDto contacts = new ContactsDto();
        contacts.setPhone("+375 17 309-80-00");
        contacts.setEmail("doubletreeminsk.info@hilton.com");
        createDto.setContacts(contacts);

        ArrivalTimeDto arrival = new ArrivalTimeDto();
        arrival.setCheckIn("14:00");
        arrival.setCheckOut("12:00");
        createDto.setArrivalTime(arrival);

        createDto.setAmenityNames(List.of("Free parking", "Free WiFi"));

        ResponseEntity<HotelDetailDto> createResponse =
                restTemplate.postForEntity(url("/hotels"), createDto, HotelDetailDto.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        HotelDetailDto created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull();
        Long id = created.getId();

        ResponseEntity<HotelDetailDto> getResponse =
                restTemplate.getForEntity(url("/hotels/" + id), HotelDetailDto.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        HotelDetailDto fetched = getResponse.getBody();
        assertThat(fetched).isNotNull();
        assertThat(fetched.getName()).isEqualTo("DoubleTree by Hilton Minsk");
        assertThat(fetched.getAddress().getCity()).isEqualTo("Minsk");

        ResponseEntity<Map> histogramResponse =
                restTemplate.exchange(url("/histogram/city"), HttpMethod.GET, HttpEntity.EMPTY, Map.class);

        assertThat(histogramResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Map<?, ?> histogram = histogramResponse.getBody();
        assertThat(histogram).isNotNull();
        // Преобразуем ключи в строки, чтобы избежать проблем с wildcard-типами
        java.util.Set<String> keys = histogram.keySet().stream()
                .map(Object::toString)
                .collect(java.util.stream.Collectors.toSet());
        assertThat(keys).contains("Minsk");
    }
}

