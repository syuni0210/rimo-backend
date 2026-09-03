package com.ansim.backend.dto;

public class AiSafeRouteRequestDto {

    private Double startLatitude;
    private Double startLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;

    // Android에서 이미 계산한 Kakao 경로
    private RouteCandidateRequestDto shortestCandidate;
    private RouteCandidateRequestDto broadCandidate;

    public AiSafeRouteRequestDto() {
    }

    public AiSafeRouteRequestDto(
            Double startLatitude,
            Double startLongitude,
            Double destinationLatitude,
            Double destinationLongitude
    ) {
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.destinationLatitude = destinationLatitude;
        this.destinationLongitude = destinationLongitude;
    }

    public Double getStartLatitude() {
        return startLatitude;
    }

    public void setStartLatitude(Double startLatitude) {
        this.startLatitude = startLatitude;
    }

    public Double getStartLongitude() {
        return startLongitude;
    }

    public void setStartLongitude(Double startLongitude) {
        this.startLongitude = startLongitude;
    }

    public Double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(Double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }

    public Double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(Double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }
    public RouteCandidateRequestDto getShortestCandidate() {
        return shortestCandidate;
    }

    public void setShortestCandidate(
            RouteCandidateRequestDto shortestCandidate
    ) {
        this.shortestCandidate = shortestCandidate;
    }


    public RouteCandidateRequestDto getBroadCandidate() {
        return broadCandidate;
    }

    public void setBroadCandidate(
            RouteCandidateRequestDto broadCandidate
    ) {
        this.broadCandidate = broadCandidate;
    }
}
