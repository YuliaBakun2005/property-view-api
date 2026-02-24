package com.example.propertyview.service;

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
import com.example.propertyview.service.impl.HotelServiceImpl;
import org.junit.jupiter.api.Test;
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

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    @Test
    void getHotelById_whenFound_returnsDetailDto() {
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
    void getHotelById_whenNotFound_throwsNotFoundException() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.getHotelById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void searchHotels_delegatesToRepositoryAndMapper() {
        Hotel hotel = new Hotel();
        hotel.setId(1L);

        HotelShortDto dto = new HotelShortDto();
        dto.setId(1L);

        when(hotelRepository.findAll(any(Specification.class))).thenReturn(List.of(hotel));
        when(hotelMapper.toShortDtoList(List.of(hotel))).thenReturn(List.of(dto));

        List<HotelShortDto> result = hotelService.searchHotels("name", "brand", "city", "country", List.of("wifi"));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
        verify(hotelRepository).findAll(any(Specification.class));
        verify(hotelMapper).toShortDtoList(List.of(hotel));
    }

    @Test
    void createHotel_withAmenities_resolvesAndSaves() {
        HotelCreateUpdateDto dto = new HotelCreateUpdateDto();
        dto.setName("Test");
        dto.setBrand("Brand");
        dto.setAmenityNames(List.of("Wifi", "Pool"));

        Hotel hotel = new Hotel();

        Amenity existingAmenity = new Amenity();
        existingAmenity.setId(1L);
        existingAmenity.setName("wifi");

        when(hotelMapper.toEntity(dto)).thenReturn(hotel);
        when(amenityRepository.findAll()).thenReturn(List.of(existingAmenity));

        ArgumentCaptor<Hotel> hotelCaptor = ArgumentCaptor.forClass(Hotel.class);

        Hotel saved = new Hotel();
        saved.setId(10L);
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(invocation -> {
            Hotel arg = invocation.getArgument(0);
            saved.setAmenities(arg.getAmenities());
            saved.setId(10L);
            return saved;
        });

        HotelDetailDto detailDto = new HotelDetailDto();
        detailDto.setId(10L);
        when(hotelMapper.toDetailDto(saved)).thenReturn(detailDto);

        HotelDetailDto result = hotelService.createHotel(dto);

        assertThat(result.getId()).isEqualTo(10L);

        verify(hotelRepository).save(hotelCaptor.capture());
        Set<Amenity> amenities = hotelCaptor.getValue().getAmenities();
        assertThat(amenities)
                .extracting(Amenity::getName)
                .map(String::toLowerCase)
                .containsExactlyInAnyOrder("wifi", "pool");
    }

    @Test
    void addAmenities_whenRequestNull_throwsBadRequest() {
        assertThatThrownBy(() -> hotelService.addAmenities(1L, null))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getHistogram_forBrand_returnsCountsFromRepository() {
        HotelRepository.ValueCount vc = new HotelRepository.ValueCount() {
            @Override
            public String getValue() {
                return "Hilton";
            }

            @Override
            public Long getCount() {
                return 3L;
            }
        };

        when(hotelRepository.countByBrand()).thenReturn(List.of(vc));

        Map<String, Long> result = hotelService.getHistogram(HistogramParam.BRAND);

        assertThat(result).containsEntry("Hilton", 3L);
    }
}

