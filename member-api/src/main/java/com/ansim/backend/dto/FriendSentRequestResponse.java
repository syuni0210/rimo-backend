package com.ansim.backend.dto;

import java.time.LocalDateTime;

public record FriendSentRequestResponse(
        Long friendId,
        Long requestMemberId,
        Long receiveMemberId,
        String receiverName,
        String receiverLoginId,
        String statusCode,
        LocalDateTime requestDate
) {
}
