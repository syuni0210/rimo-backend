package com.ansim.backend.controller;

import com.ansim.backend.dto.FriendLocationResponse;
import com.ansim.backend.dto.EmergencyPopupResponse;
import com.ansim.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.ansim.backend.dto.SharingFriendResponse; // DTO 패키지 경로에 맞게 확인

@RestController
@RequestMapping("/api/tracking")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService trackingService;

    @GetMapping("/friend/{friendId}")
    public ResponseEntity<FriendLocationResponse> getFriendLocation(
            @PathVariable Long friendId,
            @RequestParam Long requesterId) {
        FriendLocationResponse response = trackingService.getFriendLocation(requesterId, friendId);
        return ResponseEntity.ok(response);
    }

    // 위치 공유가 켜진 모든 친구의 위치 일괄 조회
    @GetMapping("/sharing-friends")
    public ResponseEntity<List<SharingFriendResponse>> getSharingFriendsLocations(
            @RequestParam("requesterId") Long requesterId) {
        List<SharingFriendResponse> response = trackingService.getSharingFriendsLocations(requesterId);
        return ResponseEntity.ok(response);
    }

    // ========================================
    // 내가 위치를 공유 중인 친구 수 조회
    // ========================================
    @GetMapping("/sharing-count")
    public ResponseEntity<Integer> getSharingCount(
            @RequestParam("memberId") Long memberId) {
        int count = trackingService.getSharingCount(memberId);
        return ResponseEntity.ok(count);
    }

    // ========================================
    // 현재 사용자에게 대기 중인 긴급 팝업 조회
    // ========================================
    @GetMapping("/emergency/pending")
    public ResponseEntity<EmergencyPopupResponse> getPendingEmergencyPopup(
            @RequestParam("memberId") Long memberId
    ) {
        EmergencyPopupResponse response =
                trackingService.getPendingEmergencyPopup(
                        memberId
                );
        return ResponseEntity.ok(
                response
        );
    }

    // ========================================
    // 긴급 팝업 확인
    // ========================================
    @PostMapping("/emergency/{emergencyId}/ack")
    public ResponseEntity<Void> acknowledgeEmergencyPopup(
            @PathVariable Long emergencyId,
            @RequestParam("memberId") Long memberId
    ) {
        trackingService.acknowledgeEmergencyPopup(
                memberId,
                emergencyId
        );
        return ResponseEntity.ok().build();
    }
// ========================================
    // 긴급 웹페이지에서 UUID로 실시간 위치 조회
    // ========================================
    @CrossOrigin("*") //  1. 추가: 웹에서 접근할 수 있도록 허용
    @GetMapping("/emergency/location/{uuid}") //  2. 수정: 앞에 /emergency 를 꼭 붙여주세요! (프론트엔드 주소와 일치시킴)
    public ResponseEntity<java.util.Map<String, Double>> getEmergencyLocationByUuid(@PathVariable String uuid) {
        java.util.Map<String, Double> location = trackingService.getEmergencyLocation(uuid);
        
        if (location == null) {
            return ResponseEntity.notFound().build(); // 데이터가 없으면 404 (만료 처리)
        }
        return ResponseEntity.ok(location);
    }
}
