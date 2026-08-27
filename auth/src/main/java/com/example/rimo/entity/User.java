package com.example.rimo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "USR")
@Getter @Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MMBR_ID")
    private Long mmbrId;

    @Column(name = "LGN_ID", nullable = false, unique = true, length = 50)
    private String lgnId;

    @Column(name = "PWD", nullable = false, length = 255)
    private String pwd;

    @Column(name = "MMBR_NM", nullable = false, length = 50)
    private String mmbrNm;

    @Column(name = "EMAIL", nullable = false, length = 255)
    private String email;

    @Column(name = "PRFL_CLR", length = 20)
    private String prflClr = "#A692FE";

    @Column(name = "MMBR_STTS_CD", length = 10, nullable = false)
    private String mmbrSttsCd = "M001";

    @Column(name = "RGSTR_DT")
    private LocalDateTime rgstrDt = LocalDateTime.now();
}
