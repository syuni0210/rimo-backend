package com.ansim.backend.repository;

import com.ansim.backend.entity.Jrny;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface JrnyRepository extends JpaRepository<Jrny, Long> {

    // ========================================
    // 회원의 전체 귀가 횟수
    // ========================================

    long countByMmbrId(Long mmbrId);


    // ========================================
    // 경로 선호도
    // ========================================

    @Query("""
        SELECT j.pthTypCd, COUNT(j)
        FROM Jrny j
        WHERE j.mmbrId = :memberId
        GROUP BY j.pthTypCd
    """)
    List<Object[]> countByPathType(
            @Param("memberId")
            Long memberId
    );


    // ========================================
    // 특정 날짜의 귀가 기록
    // ========================================

    @Query("""
        SELECT j
        FROM Jrny j
        WHERE j.mmbrId = :memberId
        AND FUNCTION(
            'DATE',
            COALESCE(j.endDt, j.strtDt)
        ) = :date
        ORDER BY COALESCE(j.endDt, j.strtDt) DESC
    """)
    List<Jrny> findByMemberIdAndDate(
            @Param("memberId")
            Long memberId,

            @Param("date")
            LocalDate date
    );


    // ========================================
    // 회원의 전체 귀가 기록
    // ========================================

    List<Jrny> findByMmbrIdOrderByEndDtDesc(
            Long mmbrId
    );


    // ========================================
    // 이번 주 앱을 가장 많이 사용한 친구
    //
    // 1. FRND에서 내 친구 목록 추출
    // 2. USR에서 친구 이름 조회
    // 3. JRNY에서 이번 주 사용 횟수 계산
    // 4. 사용 횟수가 가장 높은 친구 1명
    // ========================================

    @Query(
        value = """
            SELECT
                U.MMBR_ID,
                U.MMBR_NM,
                COUNT(J.JRNY_ID) AS USE_CNT
            FROM
            (
                SELECT DISTINCT
                    CASE
                        WHEN F.RQST_MMBR_ID = :memberId
                            THEN F.RCV_MMBR_ID
                        ELSE F.RQST_MMBR_ID
                    END AS FRIEND_ID
                FROM FRND F
                WHERE
                    (
                        F.RQST_MMBR_ID = :memberId
                        OR
                        F.RCV_MMBR_ID = :memberId
                    )
                    AND F.STTS_CD = 'F001'
                    AND COALESCE(F.DLT_YN, 'N') = 'N'
            ) FRIENDS

            JOIN USR U
                ON U.MMBR_ID = FRIENDS.FRIEND_ID

            LEFT JOIN JRNY J
                ON J.MMBR_ID = U.MMBR_ID
                AND J.STRT_DT >= :weekStart
                AND J.STRT_DT < :weekEnd

            GROUP BY
                U.MMBR_ID,
                U.MMBR_NM

            ORDER BY
                USE_CNT DESC,
                U.MMBR_ID ASC

            LIMIT 1
        """,
        nativeQuery = true
    )
    List<Object[]> findTopFriendOfWeek(
            @Param("memberId")
            Long memberId,

            @Param("weekStart")
            LocalDateTime weekStart,

            @Param("weekEnd")
            LocalDateTime weekEnd
    );
}
