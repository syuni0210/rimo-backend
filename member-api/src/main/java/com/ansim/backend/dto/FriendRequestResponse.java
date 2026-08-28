package com.ansim.backend.dto;

import java.time.LocalDateTime;

public record FriendRequestResponse(
        Long friendId,
        Long requestMemberId,
        Long receiveMemberId,
        String requesterName,
        String requesterLoginId,
        String statusCode,
        LocalDateTime requestDate
) {
}
