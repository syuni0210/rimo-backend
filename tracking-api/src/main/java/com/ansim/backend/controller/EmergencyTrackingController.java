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

    @CrossOrigin("*")
    @GetMapping("/location/{trackingId}")
    public ResponseEntity<?> getEmergencyLocation(@PathVariable String trackingId) {
        
        // 1. UUID -> memberId 검증 (이동기 님이 의도하신 보안 로직)
        String memberIdStr = redisTemplate.opsForValue().get("emergency_tracking:" + trackingId);
        if (memberIdStr == null) {
            return ResponseEntity.status(403).body(Map.of("error", "만료되거나 유효하지 않은 긴급 추적 링크입니다."));
        }

        // 2. 해당 회원의 실시간 위치 조회
        String locationKey = "member_location:" + memberIdStr; 
        Object latObj = redisTemplate.opsForHash().get(locationKey, "lat");
        Object lngObj = redisTemplate.opsForHash().get(locationKey, "lng");

        if (latObj == null || lngObj == null) {
            return ResponseEntity.status(404).body(Map.of("error", "현재 위치 정보를 수신 대기 중입니다."));
        }

        //  [핵심 해결] 문자열을 확실한 '숫자(Double)'로 강제 변환하여 카카오맵 에러 원천 차단!
        Double lat = Double.parseDouble(latObj.toString());
        Double lng = Double.parseDouble(lngObj.toString());

        return ResponseEntity.ok(Map.of(
                "lat", lat,
                "lng", lng
        ));
    }
}
