package com.ansim.backend.controller;

import com.ansim.backend.dto.AiSafeRouteRequestDto;
import com.ansim.backend.dto.AiSafeRouteResponseDto;
import com.ansim.backend.service.AiSafeRouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
