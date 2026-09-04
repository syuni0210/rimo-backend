package com.ansim.backend.service;

import com.ansim.backend.dto.FriendRequestResponse;
import com.ansim.backend.dto.FriendSentRequestResponse;
import com.ansim.backend.entity.Friend;
import com.ansim.backend.entity.Usr;
import com.ansim.backend.repository.FriendRepository;
import com.ansim.backend.repository.UsrRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import com.ansim.backend.dto.FriendListItemDto;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final UsrRepository usrRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public Friend sendRequest(Long requesterId, Long receiverId) {
        if (requesterId.equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자기 자신에게 친구 요청을 보낼 수 없습니다.");
        }
        if (!usrRepository.existsByMmbrId(requesterId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 보내는 회원이 존재하지 않습니다.");
        }
        if (!usrRepository.existsByMmbrId(receiverId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 받을 회원이 존재하지 않습니다.");
        }
        if (friendRepository.countActiveRelationship(requesterId, receiverId) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 친구이거나 처리 중인 친구 요청이 있습니다.");
        }
        Friend friend = new Friend(requesterId, receiverId);
        return friendRepository.save(friend);
    }

    public List<FriendSentRequestResponse> getSentPendingRequests(Long memberId) {
        return friendRepository.findByRequestMemberIdAndStatusCodeAndDeleteYn(memberId, "F001", "N")
                .stream()
                .map(friend -> {
                    Usr receiver = usrRepository.findById(friend.getReceiveMemberId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 받은 회원을 찾을 수 없습니다."));
                    return new FriendSentRequestResponse(
                            friend.getFriendId(), friend.getRequestMemberId(), friend.getReceiveMemberId(),
                            receiver.getMemberName(), receiver.getLoginId(), friend.getStatusCode(), friend.getRequestDate()
                    );
                })
                .toList();
    }

    public List<FriendRequestResponse> getReceivedPendingRequests(Long memberId) {
        return friendRepository.findByReceiveMemberIdAndStatusCodeAndDeleteYn(memberId, "F001", "N")
                .stream()
                .map(friend -> {
                    Usr requester = usrRepository.findById(friend.getRequestMemberId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "요청 회원을 찾을 수 없습니다."));
                    return new FriendRequestResponse(
                            friend.getFriendId(), friend.getRequestMemberId(), friend.getReceiveMemberId(),
                            requester.getMemberName(), requester.getLoginId(), friend.getStatusCode(), friend.getRequestDate()
                    );
                })
                .toList();
    }

    @Transactional
    public Friend acceptRequest(Long friendId, Long memberId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."));
        if ("Y".equals(friend.getDeleteYn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 친구 요청입니다.");
        }
        if (!friend.getReceiveMemberId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "친구 요청을 받은 회원만 수락할 수 있습니다.");
        }
        if (!"F001".equals(friend.getStatusCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대기 중인 친구 요청이 아닙니다.");
        }
        friend.accept();
        return friendRepository.save(friend);
    }

    @Transactional
    public Friend rejectRequest(Long friendId, Long memberId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."));
        if ("Y".equals(friend.getDeleteYn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제된 친구 요청입니다.");
        }
        if (!friend.getReceiveMemberId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "친구 요청을 받은 회원만 거절할 수 있습니다.");
        }
        if (!"F001".equals(friend.getStatusCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대기 중인 친구 요청이 아닙니다.");
        }
        friend.reject();
        return friendRepository.save(friend);
    }

    @Transactional
    public Friend cancelRequest(Long friendId, Long memberId) {
        Friend friend = friendRepository.findById(friendId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 요청을 찾을 수 없습니다."));
        if ("Y".equals(friend.getDeleteYn())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 취소되거나 삭제된 친구 요청입니다.");
        }
        if (!friend.getRequestMemberId().equals(memberId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "친구 요청을 보낸 회원만 취소할 수 있습니다.");
        }
        if (!"F001".equals(friend.getStatusCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "대기 중인 친구 요청만 취소할 수 있습니다.");
        }
        friend.delete();
        return friendRepository.save(friend);
    }

    public List<Usr> getFriendList(Long memberId) {
        if (!usrRepository.existsByMmbrId(memberId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "회원이 존재하지 않습니다.");
        }
        List<Friend> relationships = friendRepository.findAcceptedFriends(memberId);
        List<Usr> friends = new ArrayList<>();
        for (Friend relationship : relationships) {
            Long friendMemberId = relationship.getRequestMemberId().equals(memberId)
                    ? relationship.getReceiveMemberId()
                    : relationship.getRequestMemberId();
            usrRepository.findById(friendMemberId)
                    .filter(user -> "Y".equals(user.getUseYn()) && "N".equals(user.getDeleteYn()))
                    .ifPresent(friends::add);
        }
        return friends;
    }

    @Transactional
    public Friend deleteFriend(Long memberId, Long friendMemberId) {
        if (memberId.equals(friendMemberId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "자기 자신을 친구에서 삭제할 수 없습니다.");
        }
        Friend relationship = friendRepository.findAcceptedRelationship(memberId, friendMemberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "친구 관계를 찾을 수 없습니다."));
        relationship.delete();
        return friendRepository.save(relationship);
    }

    @Transactional
    public Friend toggleLocationSharing(Long memberId, Long friendMemberId, boolean isSharing) {
        // 1. D-cloud(RDB)에서 두 사람 간의 '수락된(Accepted)' 친구 관계 조회 (기존에 만들어둔 메서드 재사용)
        Friend relationship = friendRepository.findAcceptedRelationship(memberId, friendMemberId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "수락된 친구 관계를 찾을 수 없습니다."));

        // 2. RDB 엔티티 상태 업데이트
        // ⚠️ 주의: 이 부분이 작동하려면 Friend 엔티티 클래스 내부에 위치 공유 여부를 저장하는 필드(예: locationShareYn)와 Setter가 있어야 합니다!
        relationship.updateLocationShareStatus(memberId, isSharing); 

        // 3. Redis에 실시간 캐싱
        // Key 설계: "location:status:내회원ID:상대방회원ID" (내가 이 특정 친구에게 내 위치를 공유하는 상태)
        String redisKey = "location_share:" + memberId + ":" + friendMemberId;
        String statusValue = isSharing ? "Y" : "N";
        
        redisTemplate.opsForValue().set(redisKey, statusValue);

        return friendRepository.save(relationship);
    }
public List<FriendListItemDto> getFriendListWithSharing(Long memberId) {

    List<Usr> friends = getFriendList(memberId);

    return friends.stream()
            .map(friend -> {
                String redisKey = "location_share:" + memberId + ":" + friend.getMmbrId();
                String value = redisTemplate.opsForValue().get(redisKey);
                boolean isSharing = "Y".equals(value);

                return new FriendListItemDto(
                        friend.getMmbrId(),
                        friend.getMemberName(),
                        friend.getLoginId(),
                        isSharing
                );
            })
            .collect(Collectors.toList());
}
}
