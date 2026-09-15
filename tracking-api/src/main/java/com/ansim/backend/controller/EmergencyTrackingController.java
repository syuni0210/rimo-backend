package com.ansim.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tracking/emergency")
@RequiredArgsConstructor
public class EmergencyTrackingController {

    private final StringRedisTemplate redisTemplate;

    // 보호자가 받은 문자의 링크(웹페이지)에서 1초마다 호출할 API
    @GetMapping("/location/{trackingId}")
    public ResponseEntity<?> getEmergencyLocation(@PathVariable String trackingId) {
        
        // 1. UUID 열쇠가 유효한지(12시간 이내인지) 확인
        String memberIdStr = redisTemplate.opsForValue().get("emergency_tracking:" + trackingId);
        
        if (memberIdStr == null) {
            return ResponseEntity.status(403).body(Map.of("error", "만료되거나 유효하지 않은 긴급 추적 링크입니다."));
        }

        // 2. 이동기 님이 LocationShareService에서 저장하시는 키 이름 그대로 조회!
        String locationKey = "member_location:" + memberIdStr; 

        Object lat = redisTemplate.opsForHash().get(locationKey, "lat");
        Object lng = redisTemplate.opsForHash().get(locationKey, "lng");

        if (lat == null || lng == null) {
            return ResponseEntity.status(404).body(Map.of("error", "현재 위치 정보를 수신 대기 중입니다."));
        }

        // 3. 토글 ON/OFF 검사 로직(Bypass) 없이 무조건 최신 위치 반환
        return ResponseEntity.ok(Map.of(
                "lat", lat,
                "lng", lng
        ));
    }
}
