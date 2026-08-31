package com.ansim.backend.repository;

import com.ansim.backend.dto.DestinationCreateRequestDto;
import com.ansim.backend.dto.DestinationResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DestinationRepository {

    private final JdbcTemplate jdbcTemplate;

    public DestinationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 기본 목적지 목록 조회
    public List<DestinationResponseDto> findByMemberId(Long memberId) {

        String sql = """
                SELECT
                    DSTN_ID,
                    MMBR_ID,
                    DSTN_NM,
                    PLC_NM,
                    ADDR,
                    LAT,
                    LNG
                FROM DFLT_DSTN
                WHERE MMBR_ID = ?
                ORDER BY DSTN_ID ASC
                """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> new DestinationResponseDto(
                        rs.getLong("DSTN_ID"),
                        rs.getLong("MMBR_ID"),
                        rs.getString("DSTN_NM"),
                        rs.getString("PLC_NM"),
                        rs.getString("ADDR"),
                        rs.getDouble("LAT"),
                        rs.getDouble("LNG")
                ),
                memberId
        );
    }

    // 기본 목적지 등록
    public int save(
            Long memberId,
            DestinationCreateRequestDto request
    ) {

        String sql = """
                INSERT INTO DFLT_DSTN
                (
                    MMBR_ID,
                    DSTN_NM,
                    PLC_NM,
                    ADDR,
                    LAT,
                    LNG
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        return jdbcTemplate.update(
                sql,
                memberId,
                request.getName(),
                request.getPlaceName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude()
        );
    }

    // 기본 목적지 삭제
    public int delete(
            Long memberId,
            Long destinationId
    ) {

        String sql = """
                DELETE FROM DFLT_DSTN
                WHERE DSTN_ID = ?
                  AND MMBR_ID = ?
                """;

        return jdbcTemplate.update(
                sql,
                destinationId,
                memberId
        );
    }
}
