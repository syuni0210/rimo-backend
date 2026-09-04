package com.ansim.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "FRND")
@Getter
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRND_ID")
    private Long friendId;

    @Column(name = "RQST_MMBR_ID", nullable = false)
    private Long requestMemberId;

    @Column(name = "RCV_MMBR_ID", nullable = false)
    private Long receiveMemberId;

    @Column(name = "STTS_CD", nullable = false)
    private String statusCode;

    @Column(name = "RQST_DT")
    private LocalDateTime requestDate;

    @Column(name = "ACCPT_DT")
    private LocalDateTime acceptDate;
    
    @Column(name = "DLT_YN")
    private String deleteYn;

    // 요청자의 위치 공유 상태 (기본값: N)
    @Column(name = "RQST_LOC_SHARE_YN")
    private String requesterLocationShareYn = "N";

    // 수신자의 위치 공유 상태 (기본값: N)
    @Column(name = "RCV_LOC_SHARE_YN")
    private String receiverLocationShareYn = "N";

    public Friend(Long requestMemberId, Long receiveMemberId) {
        this.requestMemberId = requestMemberId;
        this.receiveMemberId = receiveMemberId;
        this.statusCode = "F001";
        this.requestDate = LocalDateTime.now();
        this.deleteYn = "N";
    }

    // 위치 공유 상태 변경 비즈니스 로직
    public void updateLocationShareStatus(Long memberId, boolean isSharing) {
        String status = isSharing ? "Y" : "N";
        
        // 스위치를 누른 사람이 '요청자'인 경우
        if (this.requestMemberId.equals(memberId)) {
            this.requesterLocationShareYn = status;
        } 
        // 스위치를 누른 사람이 '수신자'인 경우
        else if (this.receiveMemberId.equals(memberId)) {
            this.receiverLocationShareYn = status;
        }
    }

    public void accept() {
        this.statusCode = "F002";
        this.acceptDate = LocalDateTime.now();
    }

    public void reject() {
        this.statusCode = "F003";
        this.acceptDate = null;
    }

    public void delete() {
        this.deleteYn = "Y";
    }
}
