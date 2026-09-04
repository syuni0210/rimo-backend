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
        redisTemplate.expire(key, Duration.ofSeconds(3));
    }
}
