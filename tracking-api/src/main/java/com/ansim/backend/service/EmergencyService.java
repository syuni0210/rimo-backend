package com.ansim.backend.service;

import com.ansim.backend.entity.Emergency;
import com.ansim.backend.entity.Guardian;
import com.ansim.backend.entity.GuardianNotification;
import com.ansim.backend.entity.Usr;
import com.ansim.backend.external.KakaoGeoService;
import com.ansim.backend.external.SolapiSmsService;
import com.ansim.backend.repository.EmergencyRepository;
import com.ansim.backend.repository.GuardianNotificationRepository;
import com.ansim.backend.repository.GuardianRepository;
import com.ansim.backend.repository.UsrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID; // [추가된 부분] UUID 임포트
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmergencyService {

    private final EmergencyRepository emergencyRepository;
    private final GuardianRepository guardianRepository;
    private final GuardianNotificationRepository guardianNotificationRepository;
    private final UsrRepository usrRepository;
    private final KakaoGeoService kakaoGeoService;
    private final SolapiSmsService solapiSmsService;
    private final StringRedisTemplate redisTemplate;

    public int triggerEmergency(Long memberId, BigDecimal lat, BigDecimal lng) {

        String memberName = usrRepository.findById(memberId)
                .map(Usr::getMemberName)
                .orElse("회원");

        Emergency emergency = new Emergency();
        emergency.setMemberId(memberId);
        emergency.setEmergencyTypeCode("E101");
        emergency.setLat(lat);
        emergency.setLng(lng);
        emergency.setStatusCode("E002");
        emergency.setRegisteredAt(LocalDateTime.now());
        emergency.setSentAt(LocalDateTime.now());
        emergency = emergencyRepository.save(emergency);

        // ========================================
        // 위치 공유 중인 친구들에게 긴급 팝업 이벤트 생성
        // ========================================
        Set<String> shareKeys = redisTemplate.keys("location_share:" + memberId + ":*");

        if (shareKeys != null) {
            for (String shareKey : shareKeys) {
                String isSharing = redisTemplate.opsForValue().get(shareKey);
                if (!"Y".equals(isSharing)) continue;
                String[] parts = shareKey.split(":");
                if (parts.length != 3) continue;

                try {
                    Long friendId = Long.parseLong(parts[2]);
                    String popupKey = "emergency_popup:" + friendId + ":" + emergency.getEmergencyId();

                    redisTemplate.opsForHash().put(popupKey, "senderId", memberId.toString());
                    redisTemplate.opsForHash().put(popupKey, "senderName", memberName);
                    redisTemplate.expire(popupKey, 30, TimeUnit.SECONDS);
                } catch (NumberFormatException ignored) {}
            }
        }

        // ========================================
        // [추가 및 수정된 부분] 보호자용 1회용 실시간 추적 링크 생성
        // ========================================
        
        // 1. 1회용 난수(UUID) 생성 및 Redis 저장 (12시간 유효)
        String trackingId = UUID.randomUUID().toString();
        String trackingKey = "emergency_tracking:" + trackingId;
        redisTemplate.opsForValue().set(trackingKey, memberId.toString(), 12, TimeUnit.HOURS);

        // 2. Web EC2에 띄워둔 실시간 관제 웹페이지 주소 조립
        // TODO: 아래 도메인을 현재 운영 중인 Web EC2의 실제 도메인이나 IP로 변경해주세요.
        String trackingUrl = "https://www.rimo-app.com/tracking.html?id=" + trackingId;

        // 3. 메시지 내용에 실시간 trackingUrl 반영
        String address = kakaoGeoService.toRoadAddress(lat, lng);
        String timeText = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String subject = String.format("[리모 긴급구조 요청] %s님의 긴급구조 요청!", memberName);
        
        String message = String.format(
                "%s님이 긴급구조를 요청했습니다.\n요청 시각: %s\n초기 위치: %s\n[실시간 위치 확인 링크]\n%s\n빠르게 사용자의 안전을 확인해주세요.",
                memberName, timeText, address, trackingUrl
        );

        // ========================================
        // 보호자에게 SMS 발송 및 DB 기록
        // ========================================
        List<Guardian> guardians = guardianRepository.findByMemberIdAndUseYn(memberId, "Y");
        int sentCount = 0;

        for (Guardian guardian : guardians) {
            boolean success = solapiSmsService.sendSms(guardian.getPhoneNumber(), subject, message);

            GuardianNotification notification = new GuardianNotification();
            notification.setEmergencyId(emergency.getEmergencyId());
            notification.setGuardianId(guardian.getGuardianId());
            notification.setNotificationTypeCode("N001");
            notification.setMessageContent(message);
            notification.setLat(lat);
            notification.setLng(lng);
            notification.setSendStatusCode(success ? "N102" : "N999");
            notification.setSentAt(LocalDateTime.now());
            guardianNotificationRepository.save(notification);

            if (success) sentCount++;
        }

        return sentCount;
    }
}
