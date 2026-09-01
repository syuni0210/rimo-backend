package com.ansim.backend.dto;

public class SafetyFacilitySummaryDto {

    private Integer cctvCount;
    private Integer emergencyBellCount;
    private Integer policeCount;
    private Integer safeHouseCount;
    private Integer securityLightCount;
    private Integer smartLightCount;


    public SafetyFacilitySummaryDto() {
    }


    public SafetyFacilitySummaryDto(
            Integer cctvCount,
            Integer emergencyBellCount,
            Integer policeCount,
            Integer safeHouseCount,
            Integer securityLightCount,
            Integer smartLightCount
    ) {
        this.cctvCount = cctvCount;
        this.emergencyBellCount = emergencyBellCount;
        this.policeCount = policeCount;
        this.safeHouseCount = safeHouseCount;
        this.securityLightCount = securityLightCount;
        this.smartLightCount = smartLightCount;
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
}
