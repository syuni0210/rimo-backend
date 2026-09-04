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

        // 현재 신고자가 위치 공유를 ON 한 친구 검색
        // key 구조:
        // location_share:{공유자Id}:{친구Id}
        Set<String> shareKeys =
                redisTemplate.keys(
                        "location_share:" + memberId + ":*"
                );

        if (shareKeys != null) {

            for (String shareKey : shareKeys) {

                // 실제 위치공유가 Y인 경우에만 대상
                String isSharing =
                        redisTemplate.opsForValue().get(shareKey);

                if (!"Y".equals(isSharing)) {
                    continue;
                }

                String[] parts =
                        shareKey.split(":");

                if (parts.length != 3) {
                    continue;
                }

                try {

                    Long friendId =
                            Long.parseLong(parts[2]);

                    // 친구별 + 신고별 고유 이벤트
                    String popupKey =
                            "emergency_popup:"
                                    + friendId
                                    + ":"
                                    + emergency.getEmergencyId();

                    // 팝업에 필요한 최소 정보 저장
                    redisTemplate.opsForHash().put(
                            popupKey,
                            "senderId",
                            memberId.toString()
                    );

                    redisTemplate.opsForHash().put(
                            popupKey,
                            "senderName",
                            memberName
                    );

                    // 앱을 사용하고 있지 않은 친구에게
                    // 오래된 팝업이 나중에 뜨지 않도록 30초 후 자동 삭제
                    redisTemplate.expire(
                            popupKey,
                            30,
                            TimeUnit.SECONDS
                    );

                } catch (NumberFormatException ignored) {
                }
            }
        }

        String address = kakaoGeoService.toRoadAddress(lat, lng);
        String mapUrl = kakaoGeoService.toKakaoMapUrl(lat, lng);

        String timeText = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String subject = String.format("[리모 긴급구조 요청] %s님의 긴급구조 요청!", memberName);
        String message = String.format(
                "%s님이 긴급구조를 요청했습니다.\n요청 시각: %s\n현재 위치: %s\n현재 위치 확인\n%s\n빠르게 사용자의 안전을 확인해주세요.",
                memberName, timeText, address, mapUrl
        );

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
