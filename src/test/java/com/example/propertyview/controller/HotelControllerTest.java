package com.example.propertyview.controller;

import com.example.propertyview.dto.HotelCreateUpdateDto;
import com.example.propertyview.dto.HotelDetailDto;
import com.example.propertyview.dto.HotelShortDto;
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
import static org.hamcrest.Matchers.notNullValue;
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

    @Test
    void getAllHotels_returnsListOfShortDtos() throws Exception {
        HotelShortDto dto = new HotelShortDto();
        dto.setId(1L);
        dto.setName("Hotel");
        dto.setBrand("Brand");
        dto.setCity("City");
        dto.setCountry("Country");

        when(hotelService.getAllHotels()).thenReturn(List.of(dto));

        mockMvc.perform(get("/hotels"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Hotel")))
                .andExpect(jsonPath("$[0].brand", is("Brand")));
    }

    @Test
    void createHotel_withValidBody_returnsCreated() throws Exception {
        HotelCreateUpdateDto createDto = new HotelCreateUpdateDto();
        createDto.setName("Hotel");
        createDto.setBrand("Brand");

        // address помечен @NotNull, поэтому для успешной валидации достаточно пустого объекта
        com.example.propertyview.dto.AddressDto addressDto = new com.example.propertyview.dto.AddressDto();
        createDto.setAddress(addressDto);

        HotelDetailDto detailDto = new HotelDetailDto();
        detailDto.setId(1L);
        detailDto.setName("Hotel");

        when(hotelService.createHotel(any(HotelCreateUpdateDto.class))).thenReturn(detailDto);

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Hotel")));
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
        // name и brand не заданы, должна сработать валидация

        Mockito.reset(hotelService);

        mockMvc.perform(post("/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.status", is(400)));
    }
}

