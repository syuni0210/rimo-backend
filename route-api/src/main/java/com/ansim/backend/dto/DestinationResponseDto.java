package com.ansim.backend.dto;

public class DestinationResponseDto {

    private Long destinationId;
    private Long memberId;
    private String name;
    private String placeName;
    private String address;
    private Double latitude;
    private Double longitude;

    public DestinationResponseDto(
            Long destinationId,
            Long memberId,
            String name,
            String placeName,
            String address,
            Double latitude,
            Double longitude
    ) {
        this.destinationId = destinationId;
        this.memberId = memberId;
        this.name = name;
        this.placeName = placeName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public Long getMemberId() {
        return memberId;
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
