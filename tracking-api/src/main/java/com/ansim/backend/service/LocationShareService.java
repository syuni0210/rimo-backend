package com.ansim.backend.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class LocationShareService {

    private final StringRedisTemplate redisTemplate;

    public LocationShareService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateLocation(Long memberId, Double lat, Double lng) {

        String key = "member_location:" + memberId;

        Map<String, String> fields = new HashMap<>();
        fields.put("lat", String.valueOf(lat));
        fields.put("lng", String.valueOf(lng));

        redisTemplate.opsForHash().putAll(key, fields);
        
        // [수정된 부분] 안드로이드에서 1초마다 호출하지만, 
        // 통신 지연(핑 튐 현상)을 대비해 데이터는 5초간 살려둡니다.
        redisTemplate.expire(key, Duration.ofSeconds(1800));
    }
}
