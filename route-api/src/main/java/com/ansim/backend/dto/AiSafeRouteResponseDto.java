package com.ansim.backend.dto;

import java.util.List;

public class AiSafeRouteResponseDto {

    private String routeMode;

    private Integer distanceMeter;

    private Integer timeSecond;

    private Double safetyScore;

    private Integer cctvCount;

    private Integer emergencyBellCount;

    private Integer policeCount;

    private Integer safeHouseCount;

    private Integer securityLightCount;

    private Integer smartLightCount;

    private String recommendationReason;

    private List<RoutePointDto> path;

    private List<FacilityMapDto> facilities;

    private List<RouteCandidateDto> candidates;


    public AiSafeRouteResponseDto() {
    }


    public AiSafeRouteResponseDto(
            String routeMode,
            Integer distanceMeter,
            Integer timeSecond,
            Double safetyScore,
            Integer cctvCount,
            Integer emergencyBellCount,
            Integer policeCount,
            Integer safeHouseCount,
            Integer securityLightCount,
            Integer smartLightCount,
            String recommendationReason,
            List<RoutePointDto> path,
            List<RouteCandidateDto> candidates
    ) {

        this.routeMode = routeMode;
        this.distanceMeter = distanceMeter;
        this.timeSecond = timeSecond;
        this.safetyScore = safetyScore;
        this.cctvCount = cctvCount;
        this.emergencyBellCount = emergencyBellCount;
        this.policeCount = policeCount;
        this.safeHouseCount = safeHouseCount;
        this.securityLightCount = securityLightCount;
        this.smartLightCount = smartLightCount;
        this.recommendationReason = recommendationReason;
        this.path = path;
        this.candidates = candidates;
    }


    public String getRouteMode() {
        return routeMode;
    }

    public void setRouteMode(String routeMode) {
        this.routeMode = routeMode;
    }

    public Integer getDistanceMeter() {
        return distanceMeter;
    }

    public void setDistanceMeter(Integer distanceMeter) {
        this.distanceMeter = distanceMeter;
    }

    public Integer getTimeSecond() {
        return timeSecond;
    }

    public void setTimeSecond(Integer timeSecond) {
        this.timeSecond = timeSecond;
    }

    public Double getSafetyScore() {
        return safetyScore;
    }

    public void setSafetyScore(Double safetyScore) {
        this.safetyScore = safetyScore;
    }

    public Integer getCctvCount() {
        return cctvCount;
    }

    public void setCctvCount(Integer cctvCount) {
        this.cctvCount = cctvCount;
    }

    public Integer getEmergencyBellCount() {
        return emergencyBellCount;
    }

    public void setEmergencyBellCount(Integer emergencyBellCount) {
        this.emergencyBellCount = emergencyBellCount;
    }

    public Integer getPoliceCount() {
        return policeCount;
    }

    public void setPoliceCount(Integer policeCount) {
        this.policeCount = policeCount;
    }

    public Integer getSafeHouseCount() {
        return safeHouseCount;
    }

    public void setSafeHouseCount(Integer safeHouseCount) {
        this.safeHouseCount = safeHouseCount;
    }

    public Integer getSecurityLightCount() {
        return securityLightCount;
    }

    public void setSecurityLightCount(Integer securityLightCount) {
        this.securityLightCount = securityLightCount;
    }

    public Integer getSmartLightCount() {
        return smartLightCount;
    }

    public void setSmartLightCount(Integer smartLightCount) {
        this.smartLightCount = smartLightCount;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public List<RoutePointDto> getPath() {
        return path;
    }

    public void setPath(List<RoutePointDto> path) {
        this.path = path;
    }

    public List<FacilityMapDto> getFacilities() {
    return facilities;
    }

    public void setFacilities(List<FacilityMapDto> facilities) {
    this.facilities = facilities;
    }

    public List<RouteCandidateDto> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<RouteCandidateDto> candidates) {
        this.candidates = candidates;
    }
}
