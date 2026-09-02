package com.ansim.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "EMRGNCY")
@Getter
@Setter
@NoArgsConstructor
public class Emergency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EMRGNCY_ID")
    private Long emergencyId;

    @Column(name = "MMBR_ID", nullable = false)
    private Long memberId;

    @Column(name = "JRNY_ID")
    private Long journeyId;

    @Column(name = "EMRGNCY_TYP_CD", nullable = false, length = 10)
    private String emergencyTypeCode;

    @Column(name = "LAT", nullable = false)
    private BigDecimal lat;

    @Column(name = "LNG", nullable = false)
    private BigDecimal lng;

    @Column(name = "STTS_CD", nullable = false, length = 10)
    private String statusCode;

    @Column(name = "RGSTR_DT", nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "SND_DT")
    private LocalDateTime sentAt;
}
