package com.ansim.backend.controller;

import com.ansim.backend.dto.LocationUpdateRequest;
import com.ansim.backend.service.LocationShareService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking")
public class LocationController {

    private final LocationShareService locationShareService;

    public LocationController(LocationShareService locationShareService) {
        this.locationShareService = locationShareService;
    }

    // ========================================
    // GPS 위치 갱신 (위치공유용)
    // 3초마다 안드로이드에서 호출
    // ========================================

    @PostMapping("/location")
    public Map<String, Object> updateLocation(
            @RequestBody LocationUpdateRequest request
    ) {

        locationShareService.updateLocation(
                request.getMemberId(),
                request.getLat(),
                request.getLng()
        );

        return Map.of("success", true);
    }
}
