package com.ansim.backend.service;

import com.ansim.backend.dto.FriendLocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.ansim.backend.dto.SharingFriendResponse;
import com.ansim.backend.repository.TrackingRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TrackingService {

    private final StringRedisTemplate redisTemplate;
    private final TrackingRepository trackingRepository;

    public FriendLocationResponse getFriendLocation(Long requesterId, Long friendId) {
        // 1. Redis에서 위치 공유 허용 여부 확인 ("Y" 인지 검증)
        String shareKey = "location_share:" + friendId + ":" + requesterId;
        String isSharing = redisTemplate.opsForValue().get(shareKey);

        if (!"Y".equals(isSharing)) {
            return new FriendLocationResponse(false, 0.0, 0.0, "위치 공유가 꺼져 있거나 권한이 없습니다.");
        }

        // 2. Redis에서 최신 위치 좌표(Hash) 조회
        String locKey = "member_location:" + friendId;
        Object latObj = redisTemplate.opsForHash().get(locKey, "lat");
        Object lngObj = redisTemplate.opsForHash().get(locKey, "lng");

        if (latObj == null || lngObj == null) {
            return new FriendLocationResponse(false, 0.0, 0.0, "친구의 실시간 위치 정보를 찾을 수 없습니다.");
        }

        // 3. String 객체를 Double로 변환하여 반환
        try {
            double lat = Double.parseDouble(latObj.toString());
            double lng = Double.parseDouble(lngObj.toString());
            return new FriendLocationResponse(true, lat, lng, "성공");
        } catch (NumberFormatException e) {
            return new FriendLocationResponse(false, 0.0, 0.0, "좌표 데이터 오류");
        }
    }



// ... 기존 getFriendLocation 메서드 유지 ...

    public List<SharingFriendResponse> getSharingFriendsLocations(Long requesterId) {
        List<SharingFriendResponse> result = new ArrayList<>();
        
        Set<String> keys = redisTemplate.keys("location_share:*:" + requesterId);
        if (keys == null || keys.isEmpty()) {
            return result;
        }

        for (String key : keys) {
            String isSharing = redisTemplate.opsForValue().get(key);
            if ("Y".equals(isSharing)) {
                String[] parts = key.split(":");
                if (parts.length == 3) {
                    try {
                        Long friendId = Long.parseLong(parts[1]);
                        
                        String locKey = "member_location:" + friendId;
                        Object latObj = redisTemplate.opsForHash().get(locKey, "lat");
                        Object lngObj = redisTemplate.opsForHash().get(locKey, "lng");

                        if (latObj != null && lngObj != null) {
                            double lat = Double.parseDouble(latObj.toString());
                            double lng = Double.parseDouble(lngObj.toString());
                            
                            // ⭐️ DB에서 진짜 이름 조회
                            String friendName = trackingRepository.findMemberNameById(friendId);

                            result.add(new SharingFriendResponse(friendId, friendName, lat, lng));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}

