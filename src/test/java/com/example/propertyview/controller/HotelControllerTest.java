package com.example.propertyview.controller;

import com.example.propertyview.dto.create.AddAmenitiesRequestDto;
import com.example.propertyview.dto.create.HotelCreateUpdateDto;
import com.example.propertyview.dto.read.HotelDetailDto;
import com.example.propertyview.dto.read.HotelShortDto;
import com.example.propertyview.service.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HotelController.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HotelService hotelService;

    private HotelShortDto sampleShortDto(Long id) {
        HotelShortDto dto = new HotelShortDto();
        dto.setId(id != null ? id : 1L);
        dto.setName("Hotel");
        dto.setBrand("Brand");
        dto.setCity("City");
        dto.setCountry("Country");
        return dto;
    }

    private HotelCreateUpdateDto sampleCreateDto() {
        HotelCreateUpdateDto dto = new HotelCreateUpdateDto();
        dto.setName("Hotel");
        dto.setBrand("Brand");
        dto.setAddress(new com.example.propertyview.dto.common.AddressDto());
        return dto;
    }

    private HotelDetailDto sampleDetailDto(long id) {
        HotelDetailDto d = new HotelDetailDto();
        d.setId(id);
        d.setName("Hotel");
        return d;
    }

    @Test
    void getAllHotels_returnsListOfShortDtos() throws Exception {
        when(hotelService.getAllHotels()).thenReturn(List.of(sampleShortDto(1L)));

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Hotel")));
    }

    @Test
    void createHotel_withValidBody_returnsCreated() throws Exception {
        HotelCreateUpdateDto createDto = sampleCreateDto();
        HotelDetailDto detailDto = sampleDetailDto(1L);
        when(hotelService.createHotel(any(HotelCreateUpdateDto.class))).thenReturn(detailDto);

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void histogram_returnsMap() throws Exception {
        when(hotelService.getHistogram(eq(com.example.propertyview.service.HistogramParam.BRAND)))
                .thenReturn(Map.of("Hilton", 2L));

        mockMvc.perform(get("/histogram/brand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Hilton", is(2)));
    }

    @Test
    void createHotel_withInvalidBody_returnsBadRequest() throws Exception {
        HotelCreateUpdateDto invalidDto = new HotelCreateUpdateDto();
        Mockito.reset(hotelService);
        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getHotelById_callsService() throws Exception {
        when(hotelService.getHotelById(5L)).thenReturn(sampleDetailDto(5L));

        mockMvc.perform(get("/hotels/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)));
    }

    @Test
    void searchHotels_forwardsParameters() throws Exception {
        when(hotelService.searchHotels(eq("n"), eq("b"), eq("c"), eq("co"), any()))
                .thenReturn(List.of(sampleShortDto(7L)));

        mockMvc.perform(get("/search")
                        .param("name", "n")
                        .param("brand", "b")
                        .param("city", "c")
                        .param("country", "co"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(7)));
    }

    @Test
    void addAmenities_endpoint() throws Exception {
        AddAmenitiesRequestDto req = new AddAmenitiesRequestDto();
        req.setAmenityNames(List.of("spa"));
        when(hotelService.addAmenities(eq(9L), any(AddAmenitiesRequestDto.class)))
                .thenReturn(sampleDetailDto(9L));

        mockMvc.perform(post("/hotels/9/amenities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(9)));
    }

    @Test
    void histogram_returnsMapForCity() throws Exception {
        when(hotelService.getHistogram(eq(com.example.propertyview.service.HistogramParam.CITY)))
                .thenReturn(Map.of("Rome", 1L));

        mockMvc.perform(get("/histogram/city"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Rome", is(1)));
    }
}

