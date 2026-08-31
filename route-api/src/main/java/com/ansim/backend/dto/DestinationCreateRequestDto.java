package com.ansim.backend.dto;

public class DestinationCreateRequestDto {

    private String name;
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;

    public DestinationCreateRequestDto() {
    }

    public String getName() {
        return name;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
