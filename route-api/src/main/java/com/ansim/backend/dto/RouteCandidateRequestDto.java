// Android → Backend 전달 전용 DTO
package com.ansim.backend.dto;

import java.util.List;

public class RouteCandidateRequestDto {

    private String routeMode;

    private Integer distanceMeter;

    private Integer timeSecond;

    private List<RoutePointDto> path;


    public RouteCandidateRequestDto() {
    }


    public RouteCandidateRequestDto(
            String routeMode,
            Integer distanceMeter,
            Integer timeSecond,
            List<RoutePointDto> path
    ) {
        this.routeMode = routeMode;
        this.distanceMeter = distanceMeter;
        this.timeSecond = timeSecond;
        this.path = path;
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


    public List<RoutePointDto> getPath() {
        return path;
    }

    public void setPath(List<RoutePointDto> path) {
        this.path = path;
    }
}
