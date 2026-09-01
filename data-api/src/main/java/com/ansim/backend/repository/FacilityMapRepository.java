package com.ansim.backend.repository;

import com.ansim.backend.dto.FacilityMapDto;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FacilityMapRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;


    public FacilityMapRepository(
            NamedParameterJdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }


    // ========================================
    // 공통 BBOX 파라미터
    // ========================================

    private MapSqlParameterSource createBoundsParams(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        return new MapSqlParameterSource()

                .addValue(
                        "swLat",
                        swLat
                )

                .addValue(
                        "swLng",
                        swLng
                )

                .addValue(
                        "neLat",
                        neLat
                )

                .addValue(
                        "neLng",
                        neLng
                );
    }


    // ========================================
    // CCTV
    // D_CCTV
    // ========================================

    public List<FacilityMapDto> findCctvInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        String sql = """
            SELECT
                CCTV_ID,
                ADDRESS,
                LAT,
                LNG
            FROM D_CCTV
            WHERE US_YN = 'Y'
              AND MBRContains(
                    ST_GeomFromText(
                        CONCAT(
                            'POLYGON((',
                            :swLng, ' ', :swLat, ',',
                            :neLng, ' ', :swLat, ',',
                            :neLng, ' ', :neLat, ',',
                            :swLng, ' ', :neLat, ',',
                            :swLng, ' ', :swLat,
                            '))'
                        )
                    ),
                    GEOM
                  )
            LIMIT 1000
            """;


        return jdbcTemplate.query(

                sql,

                createBoundsParams(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                ),

                (rs, rowNum) ->

                        new FacilityMapDto(

                                rs.getLong(
                                        "CCTV_ID"
                                ),

                                "CCTV",

                                null,

                                rs.getString(
                                        "ADDRESS"
                                ),

                                rs.getDouble(
                                        "LAT"
                                ),

                                rs.getDouble(
                                        "LNG"
                                )
                        )
        );
    }


    // ========================================
    // 스마트 가로등
    // D_SMART_LIGHT
    // ========================================

    public List<FacilityMapDto> findSmartLightInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        String sql = """
            SELECT
                LGT_ID,
                ROAD_ADDRESS,
                LAT,
                LNG
            FROM D_SMART_LIGHT
            WHERE US_YN = 'Y'
              AND MBRContains(
                    ST_GeomFromText(
                        CONCAT(
                            'POLYGON((',
                            :swLng, ' ', :swLat, ',',
                            :neLng, ' ', :swLat, ',',
                            :neLng, ' ', :neLat, ',',
                            :swLng, ' ', :neLat, ',',
                            :swLng, ' ', :swLat,
                            '))'
                        )
                    ),
                    GEOM
                  )
            LIMIT 1000
            """;


        return jdbcTemplate.query(

                sql,

                createBoundsParams(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                ),

                (rs, rowNum) ->

                        new FacilityMapDto(

                                rs.getLong(
                                        "LGT_ID"
                                ),

                                "SMART_LIGHT",

                                "가로등",

                                rs.getString(
                                        "ROAD_ADDRESS"
                                ),

                                rs.getDouble(
                                        "LAT"
                                ),

                                rs.getDouble(
                                        "LNG"
                                )
                        )
        );
    }


    // ========================================
    // 안심 지킴이집
    // D_SAFE_HOUSE
    // ========================================

    public List<FacilityMapDto> findSafeHouseInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        String sql = """
            SELECT
                HOUSE_ID,
                STORE_NM,
                ROAD_ADDRESS,
                LAT,
                LNG
            FROM D_SAFE_HOUSE
            WHERE US_YN = 'Y'
              AND MBRContains(
                    ST_GeomFromText(
                        CONCAT(
                            'POLYGON((',
                            :swLng, ' ', :swLat, ',',
                            :neLng, ' ', :swLat, ',',
                            :neLng, ' ', :neLat, ',',
                            :swLng, ' ', :neLat, ',',
                            :swLng, ' ', :swLat,
                            '))'
                        )
                    ),
                    GEOM
                  )
            LIMIT 1000
            """;


        return jdbcTemplate.query(

                sql,

                createBoundsParams(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                ),

                (rs, rowNum) ->

                        new FacilityMapDto(

                                rs.getLong(
                                        "HOUSE_ID"
                                ),

                                "SAFE_HOUSE",

                                rs.getString(
                                        "STORE_NM"
                                ),

                                rs.getString(
                                        "ROAD_ADDRESS"
                                ),

                                rs.getDouble(
                                        "LAT"
                                ),

                                rs.getDouble(
                                        "LNG"
                                )
                        )
        );
    }


    // ========================================
    // 지구대 / 파출소
    // D_POLICE
    // ========================================

    public List<FacilityMapDto> findPoliceInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        String sql = """
            SELECT
                PLC_ID,
                PLC_NM,
                ROAD_ADDRESS,
                LAT,
                LNG
            FROM D_POLICE
            WHERE US_YN = 'Y'
              AND MBRContains(
                    ST_GeomFromText(
                        CONCAT(
                            'POLYGON((',
                            :swLng, ' ', :swLat, ',',
                            :neLng, ' ', :swLat, ',',
                            :neLng, ' ', :neLat, ',',
                            :swLng, ' ', :neLat, ',',
                            :swLng, ' ', :swLat,
                            '))'
                        )
                    ),
                    GEOM
                  )
            LIMIT 1000
            """;


        return jdbcTemplate.query(

                sql,

                createBoundsParams(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                ),

                (rs, rowNum) ->

                        new FacilityMapDto(

                                rs.getLong(
                                        "PLC_ID"
                                ),

                                "POLICE",

                                rs.getString(
                                        "PLC_NM"
                                ),

                                rs.getString(
                                        "ROAD_ADDRESS"
                                ),

                                rs.getDouble(
                                        "LAT"
                                ),

                                rs.getDouble(
                                        "LNG"
                                )
                        )
        );
    }


    // ========================================
    // 비상벨
    // D_EMERGENCY_BELL
    // ========================================

    public List<FacilityMapDto> findEmergencyBellInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        String sql = """
            SELECT
                BELL_ID,
                BELL_NM,
                ROAD_ADDRESS,
                LAT,
                LNG
            FROM D_EMERGENCY_BELL
            WHERE US_YN = 'Y'
              AND MBRContains(
                    ST_GeomFromText(
                        CONCAT(
                            'POLYGON((',
                            :swLng, ' ', :swLat, ',',
                            :neLng, ' ', :swLat, ',',
                            :neLng, ' ', :neLat, ',',
                            :swLng, ' ', :neLat, ',',
                            :swLng, ' ', :swLat,
                            '))'
                        )
                    ),
                    GEOM
                  )
            LIMIT 1000
            """;


        return jdbcTemplate.query(

                sql,

                createBoundsParams(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                ),

                (rs, rowNum) ->

                        new FacilityMapDto(

                                rs.getLong(
                                        "BELL_ID"
                                ),

                                "EMERGENCY_BELL",

                                rs.getString(
                                        "BELL_NM"
                                ),

                                rs.getString(
                                        "ROAD_ADDRESS"
                                ),

                                rs.getDouble(
                                        "LAT"
                                ),

                                rs.getDouble(
                                        "LNG"
                                )
                        )
        );
    }


    // ========================================
    // 보안등
    // D_SECURITY_LIGHT
    // ========================================

    public List<FacilityMapDto> findSecurityLightInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        String sql = """
            SELECT
                LIGHT_ID,
                LIGHT_NM,
                ROAD_ADDRESS,
                LAT,
                LNG
            FROM D_SECURITY_LIGHT
            WHERE US_YN = 'Y'
              AND MBRContains(
                    ST_GeomFromText(
                        CONCAT(
                            'POLYGON((',
                            :swLng, ' ', :swLat, ',',
                            :neLng, ' ', :swLat, ',',
                            :neLng, ' ', :neLat, ',',
                            :swLng, ' ', :neLat, ',',
                            :swLng, ' ', :swLat,
                            '))'
                        )
                    ),
                    GEOM
                  )
            LIMIT 1000
            """;


        return jdbcTemplate.query(

                sql,

                createBoundsParams(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                ),

                (rs, rowNum) ->

                        new FacilityMapDto(

                                rs.getLong(
                                        "LIGHT_ID"
                                ),

                                "SECURITY_LIGHT",

                                rs.getString(
                                        "LIGHT_NM"
                                ),

                                rs.getString(
                                        "ROAD_ADDRESS"
                                ),

                                rs.getDouble(
                                        "LAT"
                                ),

                                rs.getDouble(
                                        "LNG"
                                )
                        )
        );
    }
}