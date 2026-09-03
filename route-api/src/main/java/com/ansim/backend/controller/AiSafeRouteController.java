package com.ansim.backend.controller;

import com.ansim.backend.dto.AiSafeRouteRequestDto;
import com.ansim.backend.dto.AiSafeRouteResponseDto;
import com.ansim.backend.service.AiSafeRouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ansim.backend.dto.FacilityMapDto;
import com.ansim.backend.dto.RouteFacilitiesRequestDto;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class AiSafeRouteController {

    private final AiSafeRouteService aiSafeRouteService;

    public AiSafeRouteController(
            AiSafeRouteService aiSafeRouteService
    ) {
        this.aiSafeRouteService = aiSafeRouteService;
    }

    @PostMapping("/ai-safe")
    public ResponseEntity<AiSafeRouteResponseDto> findAiSafeRoute(
            @RequestBody AiSafeRouteRequestDto request
    ) {

        AiSafeRouteResponseDto response =
                aiSafeRouteService.findAiSafeRoute(request);

        return ResponseEntity.ok(response);
    }
    // ========================================
// 사용자가 실제로 선택한 경로 주변
// 50m 이내 안전시설 조회
//
// AI 계산 완료 전에 SHORTEST / BROAD_FIRST가
// 선택됐을 때 Android에서 호출합니다.
// ========================================
    @PostMapping("/facilities-near-path")
    public ResponseEntity<List<FacilityMapDto>> findFacilitiesNearPath(
            @RequestBody RouteFacilitiesRequestDto request
    ) {

        List<FacilityMapDto> facilities =
                aiSafeRouteService.getFacilitiesNearPath(
                        request.getPath()
                );

        return ResponseEntity.ok(facilities);
    }
}
