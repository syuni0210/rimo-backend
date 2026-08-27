package com.ansim.backend.repository;

import com.ansim.backend.dto.ProfileResponseDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    private final JdbcTemplate jdbcTemplate;

    public MemberRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ProfileResponseDto findProfileByMemberId(Long memberId) {

        String sql = """
                SELECT
                    MMBR_ID,
                    LGN_ID,
                    MMBR_NM,
                    EMAIL
                FROM USR
                WHERE MMBR_ID = ?
                  AND DLT_YN = 'N'
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) ->
                        new ProfileResponseDto(
                                rs.getLong("MMBR_ID"),
                                rs.getString("LGN_ID"),
                                rs.getString("MMBR_NM"),
                                rs.getString("EMAIL")
                        ),
                memberId
        );
    }

    public int updateProfile(
            Long memberId,
            String memberName,
            String email
    ) {

        String sql = """
                UPDATE USR
                SET
                    MMBR_NM = ?,
                    EMAIL = ?,
                    MDFY_DT = NOW()
                WHERE MMBR_ID = ?
                  AND DLT_YN = 'N'
                """;

        return jdbcTemplate.update(
                sql,
                memberName,
                email,
                memberId
        );
    }
}
