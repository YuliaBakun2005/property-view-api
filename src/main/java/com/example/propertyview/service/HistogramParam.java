package com.example.propertyview.service;

public enum HistogramParam {
    BRAND("brand"),
    CITY("city"),
    COUNTRY("country"),
    AMENITIES("amenities");

    private final String value;

    HistogramParam(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static HistogramParam from(String value) {
        for (HistogramParam param : values()) {
            if (param.value.equalsIgnoreCase(value)) {
                return param;
            }
        }
        throw new IllegalArgumentException("Unsupported histogram parameter: " + value);
    }
}

