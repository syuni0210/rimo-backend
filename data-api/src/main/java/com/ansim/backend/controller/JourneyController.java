package com.ansim.backend.controller;

import com.ansim.backend.dto.JourneySaveRequestDto;
import com.ansim.backend.service.JourneyService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class JourneyController {

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    // ========================================
    // 귀가 여정 저장
    // ========================================

    @PostMapping("/journeys")
    public Map<String, Object> saveJourney(
            @RequestBody JourneySaveRequestDto request
    ) {

        Long jrnyId = journeyService.saveJourney(request);

        return Map.of(
                "success", true,
                "jrnyId", jrnyId
        );
    }
}
