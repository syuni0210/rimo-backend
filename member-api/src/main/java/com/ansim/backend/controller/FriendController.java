package com.ansim.backend.controller;

import com.ansim.backend.dto.FriendRequestResponse;
import com.ansim.backend.dto.FriendSentRequestResponse;
import com.ansim.backend.entity.Friend;
import com.ansim.backend.entity.Usr;
import com.ansim.backend.service.FriendService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.ansim.backend.dto.FriendListItemDto;


@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    public Friend sendRequest(
            @RequestParam Long requesterId,
            @RequestParam Long receiverId
    ) {
        return friendService.sendRequest(requesterId, receiverId);
    }

    @GetMapping("/pending/sent")
    public List<FriendSentRequestResponse> getSentPendingRequests(
            @RequestParam Long memberId
    ) {
        return friendService.getSentPendingRequests(memberId);
    }

    @GetMapping("/pending/received")
    public List<FriendRequestResponse> getReceivedPendingRequests(
            @RequestParam Long memberId
    ) {
        return friendService.getReceivedPendingRequests(memberId);
    }

    @PatchMapping("/{friendId}/accept")
    public Friend acceptRequest(
            @PathVariable Long friendId,
            @RequestParam Long memberId
    ) {
        return friendService.acceptRequest(friendId, memberId);
    }

    @PatchMapping("/{friendId}/reject")
    public Friend rejectRequest(
            @PathVariable Long friendId,
            @RequestParam Long memberId
    ) {
        return friendService.rejectRequest(friendId, memberId);
    }

    @PatchMapping("/{friendId}/cancel")
    public Friend cancelRequest(
            @PathVariable Long friendId,
            @RequestParam Long memberId
    ) {
        return friendService.cancelRequest(friendId, memberId);
    }

    // 위치 공유 상태 변경 API (추가)
    @PatchMapping("/{friendMemberId}/location-sharing")
    public Friend toggleLocationSharing(
            @PathVariable Long friendMemberId,
            @RequestParam Long memberId,
            @RequestParam boolean isSharing
    ) {
        return friendService.toggleLocationSharing(memberId, friendMemberId, isSharing);
    }

    @GetMapping
    public List<FriendListItemDto> getFriendList(@RequestParam Long memberId) {
    return friendService.getFriendListWithSharing(memberId);
    }

    @DeleteMapping
    public Friend deleteFriend(
            @RequestParam Long memberId,
            @RequestParam Long friendMemberId
    ) {
        return friendService.deleteFriend(memberId, friendMemberId);
    }
}
