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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmergencyService {

    private final EmergencyRepository emergencyRepository;
    private final GuardianRepository guardianRepository;
    private final GuardianNotificationRepository guardianNotificationRepository;
    private final UsrRepository usrRepository;
    private final KakaoGeoService kakaoGeoService;
    private final SolapiSmsService solapiSmsService;

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
