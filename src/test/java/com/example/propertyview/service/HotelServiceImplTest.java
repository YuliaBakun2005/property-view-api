package com.example.propertyview.service;

import com.example.propertyview.dto.create.AddAmenitiesRequestDto;
import com.example.propertyview.dto.create.HotelCreateUpdateDto;
import com.example.propertyview.dto.read.HotelDetailDto;
import com.example.propertyview.dto.read.HotelShortDto;
import com.example.propertyview.entity.Amenity;
import com.example.propertyview.entity.Hotel;
import com.example.propertyview.exception.BadRequestException;
import com.example.propertyview.exception.NotFoundException;
import com.example.propertyview.mapper.HotelMapper;
import com.example.propertyview.repository.HotelRepository;
import com.example.propertyview.repository.projection.ValueCountProjection;
import com.example.propertyview.service.AmenityService;
import com.example.propertyview.service.impl.HotelServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmenityService amenityService;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    @Nested
    class GetHotelById {
        @Test
        void whenFound_returnsDetailDto() {
            Hotel hotel = new Hotel();
            hotel.setId(1L);

            HotelDetailDto detailDto = new HotelDetailDto();
            detailDto.setId(1L);

            when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
            when(hotelMapper.toDetailDto(hotel)).thenReturn(detailDto);

            HotelDetailDto result = hotelService.getHotelById(1L);
            assertThat(result.getId()).isEqualTo(1L);
            verify(hotelRepository).findById(1L);
            verify(hotelMapper).toDetailDto(hotel);
        }

        @Test
        void whenNotFound_throwsNotFoundException() {
            when(hotelRepository.findById(99L)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> hotelService.getHotelById(99L))
                    .isInstanceOf(NotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    class GetAllHotels {
        @Test
        void returnsEmptyList_whenNoHotels() {
            when(hotelRepository.findAll()).thenReturn(List.of());
            when(hotelMapper.toShortDtoList(List.of())).thenReturn(List.of());

            List<HotelShortDto> result = hotelService.getAllHotels();
            assertThat(result).isEmpty();
        }

        @Test
        void returnsMappedList() {
            Hotel hotel = sampleHotel(2L);
            HotelShortDto dto = sampleShortDto(2L);
            when(hotelRepository.findAll()).thenReturn(List.of(hotel));
            when(hotelMapper.toShortDtoList(List.of(hotel))).thenReturn(List.of(dto));

            List<HotelShortDto> result = hotelService.getAllHotels();
            assertThat(result).hasSize(1).first().extracting(HotelShortDto::getId).isEqualTo(2L);
        }
    }

    @Nested
    class SearchHotels {
        @Test
        void delegatesToRepositoryAndMapper() {
            Hotel hotel = sampleHotel(1L);
            HotelShortDto dto = sampleShortDto(1L);

            when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(hotel));
            when(hotelMapper.toShortDtoList(List.of(hotel))).thenReturn(List.of(dto));

            List<HotelShortDto> result = hotelService.searchHotels("name", "brand", "city", "country", List.of("wifi"));
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(1L);
        }

        @Test
        void allowsEmptyCriteria() {
            when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of());
            when(hotelMapper.toShortDtoList(List.of())).thenReturn(List.of());

            List<HotelShortDto> result = hotelService.searchHotels(null, null, null, null, null);
            assertThat(result).isEmpty();
        }
    }

    @Nested
    class CreateHotel {
        @Test
        void withAmenities_resolvesAndSaves() {
            HotelCreateUpdateDto dto = sampleCreateDtoWithAmenities("Wifi", "Pool");
            Hotel hotel = sampleHotel(null);

            when(hotelMapper.toEntity(dto)).thenReturn(hotel);
            stubAmenityServiceAdds(hotel, "wifi", "pool");

            Hotel saved = sampleSavedHotel(10L);
            when(hotelRepository.save(any(Hotel.class))).thenReturn(saved);
            when(hotelMapper.toDetailDto(saved)).thenReturn(sampleDetailDto(10L));

            HotelDetailDto result = hotelService.createHotel(dto);
            assertThat(result.getId()).isEqualTo(10L);
            verifySavedAmenities("wifi", "pool");
        }

        @Test
        void withoutAmenities_justSaves() {
            HotelCreateUpdateDto dto = new HotelCreateUpdateDto();
            dto.setName("NoAmenities");
            dto.setBrand("Brand");

            Hotel hotel = sampleHotel(null);
            when(hotelMapper.toEntity(dto)).thenReturn(hotel);
            when(hotelRepository.save(any(Hotel.class))).thenReturn(sampleSavedHotel(5L));
            when(hotelMapper.toDetailDto(any(Hotel.class))).thenReturn(sampleDetailDto(5L));
            
            HotelDetailDto result = hotelService.createHotel(dto);
            assertThat(result.getId()).isEqualTo(5L);
        }
    }

    @Nested
    class AddAmenitiesTests {
        @Test
        void whenRequestNull_throwsBadRequest() {
            assertThatThrownBy(() -> hotelService.addAmenities(1L, null))
                    .isInstanceOf(BadRequestException.class);
        }

        @Test
        void whenHotelMissing_throwsNotFound() {
            when(hotelRepository.findById(2L)).thenReturn(Optional.empty());

            AddAmenitiesRequestDto req = new AddAmenitiesRequestDto();
            req.setAmenityNames(List.of("wifi"));

            assertThatThrownBy(() -> hotelService.addAmenities(2L, req))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        void success_mergesList() {
            Hotel hotel = sampleHotel(3L);
            hotel.setAmenities(Set.of());
            when(hotelRepository.findById(3L)).thenReturn(Optional.of(hotel));
            when(hotelRepository.save(any(Hotel.class))).thenAnswer(inv -> inv.getArgument(0));

            AddAmenitiesRequestDto req = new AddAmenitiesRequestDto();
            req.setAmenityNames(List.of("wifi"));

            HotelDetailDto detail = new HotelDetailDto();
            detail.setId(3L);
            when(hotelMapper.toDetailDto(any(Hotel.class))).thenReturn(detail);

            HotelDetailDto result = hotelService.addAmenities(3L, req);
            assertThat(result.getId()).isEqualTo(3L);
        }
    }

    @Nested
    class HistogramTests {
        @Test
        void forBrand_returnsCounts() {
            when(hotelRepository.countByBrand())
                    .thenReturn(List.of(projection("Hilton", 3L)));
            Map<String, Long> result = hotelService.getHistogram(HistogramParam.BRAND);
            assertThat(result).containsEntry("Hilton", 3L);
        }

        @Test
        void forCity_andCountry_andAmenities() {
            when(hotelRepository.countByCity()).thenReturn(List.of(projection("Paris", 1L)));
            when(hotelRepository.countByCountry()).thenReturn(List.of(projection("France", 1L)));
            when(hotelRepository.countByAmenity()).thenReturn(List.of(projection("wifi", 4L)));

            assertThat(hotelService.getHistogram(HistogramParam.CITY)).containsKey("Paris");
            assertThat(hotelService.getHistogram(HistogramParam.COUNTRY)).containsKey("France");
            assertThat(hotelService.getHistogram(HistogramParam.AMENITIES)).containsKey("wifi");
        }
    }

    // ---------- helpers to shorten tests ----------

    private Hotel sampleHotel(Long id) {
        Hotel h = new Hotel();
        if (id != null) h.setId(id);
        return h;
    }

    private HotelShortDto sampleShortDto(Long id) {
        HotelShortDto dto = new HotelShortDto();
        if (id != null) dto.setId(id);
        return dto;
    }

    private HotelDetailDto sampleDetailDto(Long id) {
        HotelDetailDto dto = new HotelDetailDto();
        if (id != null) dto.setId(id);
        return dto;
    }

    private HotelCreateUpdateDto sampleCreateDtoWithAmenities(String... names) {
        HotelCreateUpdateDto dto = new HotelCreateUpdateDto();
        dto.setName("Test");
        dto.setBrand("Brand");
        dto.setAmenityNames(List.of(names));
        return dto;
    }

    private Hotel sampleSavedHotel(Long id) {
        Hotel h = new Hotel();
        h.setId(id);
        return h;
    }

    private ValueCountProjection projection(String v, long count) {
        return new ValueCountProjection() {
            @Override
            public String getValue() { return v; }
            @Override
            public Long getCount() { return count; }
        };
    }

    private void stubAmenityServiceAdds(Hotel hotel, String...names) {
        doAnswer(inv -> {
            Hotel h = inv.getArgument(1);
            Set<Amenity> set = Set.of(names).stream().map(n -> {
                Amenity a = new Amenity();
                a.setName(n);
                return a;
            }).collect(java.util.stream.Collectors.toSet());
            h.setAmenities(set);
            return null;
        }).when(amenityService).applyAmenitiesFromNames(any(), any());
    }

    private void verifySavedAmenities(String...expected) {
        ArgumentCaptor<Hotel> captor = ArgumentCaptor.forClass(Hotel.class);
        verify(hotelRepository).save(captor.capture());
        assertThat(captor.getValue().getAmenities())
                .extracting(Amenity::getName)
                .map(String::toLowerCase)
                .containsExactlyInAnyOrder(expected);
    }
}

