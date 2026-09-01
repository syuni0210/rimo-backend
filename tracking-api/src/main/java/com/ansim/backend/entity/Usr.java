package com.ansim.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USR")
@Getter
@NoArgsConstructor
public class Usr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MMBR_ID")
    private Long mmbrId;

    @Column(name = "LGN_ID", nullable = false, unique = true)
    private String loginId;

    @Column(name = "MMBR_NM", nullable = false)
    private String memberName;

    @Column(name = "PRFL_CLR")
    private String profileColor;

    @Column(name = "MMBR_STTS_CD")
    private String memberStatusCode;

    @Column(name = "US_YN")
    private String useYn;

    @Column(name = "DLT_YN")
    private String deleteYn;
}
