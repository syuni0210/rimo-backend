package com.ansim.backend.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TrackingRepository {

    private final JdbcTemplate jdbcTemplate;

    public TrackingRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 트래킹 화면에서 필요한 친구 이름 조회 쿼리를 여기에 집중시킵니다
    public String findMemberNameById(Long memberId) {
        String sql = """
            SELECT MMBR_NM 
            FROM USR 
            WHERE MMBR_ID = ? 
              AND (DLT_YN = 'N' OR DLT_YN IS NULL)
            """;
        try {
            return jdbcTemplate.queryForObject(sql, String.class, memberId);
        } catch (Exception e) {
            return "알 수 없음";
        }
    }
}
