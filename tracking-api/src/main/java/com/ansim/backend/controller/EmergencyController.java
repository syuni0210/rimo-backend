package com.ansim.backend.controller;

import com.ansim.backend.dto.EmergencyTriggerRequest;
import com.ansim.backend.service.EmergencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking/emergency")
@RequiredArgsConstructor
public class EmergencyController {

    private final EmergencyService emergencyService;

    @PostMapping("/trigger")
    public Map<String, Object> trigger(@RequestBody EmergencyTriggerRequest request) {

        int sentCount = emergencyService.triggerEmergency(
                request.getMemberId(), request.getLat(), request.getLng()
        );

        return Map.of(
                "success", true,
                "notifiedGuardianCount", sentCount
        );
    }
}
