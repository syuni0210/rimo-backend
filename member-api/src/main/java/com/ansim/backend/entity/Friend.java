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

    public Friend(Long requestMemberId, Long receiveMemberId) {
        this.requestMemberId = requestMemberId;
        this.receiveMemberId = receiveMemberId;
        this.statusCode = "F001";
        this.requestDate = LocalDateTime.now();
        this.deleteYn = "N";
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
