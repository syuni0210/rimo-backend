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
}