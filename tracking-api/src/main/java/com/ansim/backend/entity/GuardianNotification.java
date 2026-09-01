package com.ansim.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GRDN_NTFCTN")
@Getter
@Setter
@NoArgsConstructor
public class GuardianNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "NTFCTN_ID")
    private Long notificationId;

    @Column(name = "EMRGNCY_ID")
    private Long emergencyId;

    @Column(name = "GRDN_ID", nullable = false)
    private Long guardianId;

    @Column(name = "NTFCTN_TYP_CD", nullable = false, length = 10)
    private String notificationTypeCode;

    @Column(name = "MSG_CNTNT", nullable = false)
    private String messageContent;

    @Column(name = "LAT", nullable = false)
    private BigDecimal lat;

    @Column(name = "LNG", nullable = false)
    private BigDecimal lng;

    @Column(name = "SND_STTS_CD", nullable = false, length = 10)
    private String sendStatusCode;

    @Column(name = "SND_DT")
    private LocalDateTime sentAt;
}
