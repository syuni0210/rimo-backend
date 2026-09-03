package com.ansim.backend.dto;

import java.util.List;

/**
 * Android에서 사용자가 실제로 선택한 경로 좌표를 전달받기 위한 DTO.
 *
 * 전달받은 path를 기준으로
 * Backend가 경로 주변 50m 이내 안전시설을 조회합니다.
 */
public class RouteFacilitiesRequestDto {

    private List<RoutePointDto> path;

    public RouteFacilitiesRequestDto() {
    }

    public List<RoutePointDto> getPath() {
        return path;
    }

    public void setPath(List<RoutePointDto> path) {
        this.path = path;
    }
}