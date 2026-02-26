package com.example.propertyview.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

public class ArrivalTimeDto {

    @JsonFormat(pattern = "HH:mm")
    @Schema(example = "14:00")
    private LocalTime checkIn;

    @JsonFormat(pattern = "HH:mm")
    @Schema(example = "12:00")
    private LocalTime checkOut;

    public LocalTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalTime checkOut) {
        this.checkOut = checkOut;
    }
}

