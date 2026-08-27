package com.ansim.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "GRDN")
@Getter
@Setter
@NoArgsConstructor
public class Guardian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GRDN_ID")
    private Long guardianId;

    @Column(name = "MMBR_ID", nullable = false)
    private Long memberId;

    @Column(name = "GRDN_NM", nullable = false, length = 50)
    private String guardianName;

    @Column(name = "PHN_NO", nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "RL_NM", length = 30)
    private String relationName;

    @Column(name = "RGSTR_DT")
    private LocalDateTime registeredAt;

    @Column(name = "US_YN", length = 1)
    private String useYn;
}
