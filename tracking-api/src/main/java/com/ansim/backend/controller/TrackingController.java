package com.ansim.backend.controller;

import com.ansim.backend.dto.FriendLocationResponse;
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
}
