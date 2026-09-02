package com.ansim.backend.service;

import com.ansim.backend.dto.RoutePreferenceDto;
import com.ansim.backend.dto.ReturnRecordDto;
import com.ansim.backend.dto.SummaryDto;
import com.ansim.backend.dto.TopFriendDto;
import com.ansim.backend.entity.Jrny;
import com.ansim.backend.repository.JrnyRepository;
import org.springframework.stereotype.Service;
import com.ansim.backend.dto.FacilityMapDto;
import com.ansim.backend.repository.FacilityMapRepository;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final JrnyRepository jrnyRepository;

    private final FacilityMapRepository facilityMapRepository;


    // ========================================
    // 경로 코드명
    // ========================================

    private static final Map<String, String>
            PATH_TYPE_LABELS = Map.of(

            "R001", "빠른길",
            "R002", "밝은길",
            "R003", "대로변"
    );


    public ReportService(

            JrnyRepository jrnyRepository,

            FacilityMapRepository facilityMapRepository

    ) {

        this.jrnyRepository = jrnyRepository;

        this.facilityMapRepository =
                facilityMapRepository;
    }

    // ========================================
// 안심지도 - CCTV 조회
// ========================================

    public List<FacilityMapDto> getCctvInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        return facilityMapRepository
                .findCctvInBounds(

                        swLat,
                        swLng,

                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 스마트 가로등 조회
// ========================================

    public List<FacilityMapDto> getSmartLightInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        return facilityMapRepository
                .findSmartLightInBounds(

                        swLat,
                        swLng,

                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 지킴이집 조회
// ========================================

    public List<FacilityMapDto> getSafeHouseInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        return facilityMapRepository
                .findSafeHouseInBounds(

                        swLat,
                        swLng,

                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 지구대 / 파출소 조회
// ========================================

    public List<FacilityMapDto> getPoliceInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        return facilityMapRepository
                .findPoliceInBounds(

                        swLat,
                        swLng,

                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 비상벨 조회
// ========================================

    public List<FacilityMapDto> getEmergencyBellInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        return facilityMapRepository
                .findEmergencyBellInBounds(

                        swLat,
                        swLng,

                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 보안등 조회
// ========================================

    public List<FacilityMapDto> getSecurityLightInBounds(

            double swLat,
            double swLng,

            double neLat,
            double neLng

    ) {

        return facilityMapRepository
                .findSecurityLightInBounds(

                        swLat,
                        swLng,

                        neLat,
                        neLng
                );
    }

    // ========================================
    // 총 귀가 횟수 / 평균 소요시간
    // ========================================

    public SummaryDto getSummary(
            Long memberId
    ) {

        List<Jrny> journeys =
                jrnyRepository
                        .findByMmbrIdOrderByEndDtDesc(
                                memberId
                        );


        long total =
                journeys.size();


        double avgMin =
                journeys.stream()

                        .filter(j ->
                                j.getStrtDt() != null
                                &&
                                j.getEndDt() != null
                        )

                        .mapToLong(j ->
                                Duration
                                        .between(
                                                j.getStrtDt(),
                                                j.getEndDt()
                                        )
                                        .toMinutes()
                        )

                        .average()

                        .orElse(0.0);


        avgMin =
                Math.round(avgMin);


        return new SummaryDto(
                total,
                avgMin
        );
    }


    // ========================================
    // 경로 선호도
    // ========================================

    public List<RoutePreferenceDto>
    getRoutePreference(
            Long memberId
    ) {

        List<Object[]> rows =
                jrnyRepository
                        .countByPathType(
                                memberId
                        );


        long total =
                rows.stream()
                        .mapToLong(r ->
                                ((Number) r[1])
                                        .longValue()
                        )
                        .sum();


        return rows.stream()

                .map(r -> {

                    String code =
                            r[0] != null
                                    ? r[0].toString()
                                    : "-";


                    long count =
                            ((Number) r[1])
                                    .longValue();


                    double percent =
                            total == 0
                                    ? 0
                                    : Math.round(
                                            count
                                            * 100.0
                                            / total
                                    );


                    String label =
                            PATH_TYPE_LABELS
                                    .getOrDefault(
                                            code,
                                            code
                                    );


                    return new RoutePreferenceDto(
                            code,
                            label,
                            count,
                            percent
                    );
                })

                .collect(
                        Collectors.toList()
                );
    }


    // ========================================
    // 귀가 기록
    // ========================================

    public List<ReturnRecordDto>
    getRecords(
            Long memberId,
            String dateStr
    ) {

        List<Jrny> journeys;


        if (
                dateStr != null
                &&
                !dateStr.isBlank()
        ) {

            journeys =
                    jrnyRepository
                            .findByMemberIdAndDate(
                                    memberId,
                                    LocalDate.parse(
                                            dateStr
                                    )
                            );

        } else {

            journeys =
                    jrnyRepository
                            .findByMmbrIdOrderByEndDtDesc(
                                    memberId
                            );
        }


        DateTimeFormatter timeFmt =
                DateTimeFormatter
                        .ofPattern(
                                "HH:mm"
                        );


        return journeys.stream()

                .map(j -> {

                    ReturnRecordDto dto =
                            new ReturnRecordDto();


                    // ========================================
                    // 날짜
                    // ========================================

                    if (
                            j.getEndDt() != null
                    ) {

                        dto.setDate(
                                j.getEndDt()
                                        .toLocalDate()
                                        .toString()
                        );

                    } else if (
                            j.getStrtDt() != null
                    ) {

                        dto.setDate(
                                j.getStrtDt()
                                        .toLocalDate()
                                        .toString()
                        );

                    } else {

                        dto.setDate("-");
                    }


                    // ========================================
                    // 출발지 / 도착지
                    // ========================================

                    dto.setStartLocation(

                            j.getStrtAddrss() != null
                                    ? j.getStrtAddrss()
                                    : "-"
                    );


                    dto.setDestination(

                            j.getEndAddrss() != null
                                    ? j.getEndAddrss()
                                    : "-"
                    );


                    // ========================================
                    // 출발시간
                    // ========================================

                    dto.setStartTime(

                            j.getStrtDt() != null

                                    ? j.getStrtDt()
                                        .format(
                                                timeFmt
                                        )

                                    : "-"
                    );


                    // ========================================
                    // 도착시간
                    // ========================================

                    dto.setArrivalTime(

                            j.getEndDt() != null

                                    ? j.getEndDt()
                                        .format(
                                                timeFmt
                                        )

                                    : "-"
                    );


                    // ========================================
                    // 소요시간
                    // ========================================

                    if (
                            j.getStrtDt() != null
                            &&
                            j.getEndDt() != null
                    ) {

                        long minutes =
                                Duration
                                        .between(
                                                j.getStrtDt(),
                                                j.getEndDt()
                                        )
                                        .toMinutes();


                        dto.setDuration(
                                minutes + "분"
                        );

                    } else {

                        dto.setDuration("-");
                    }


                    // ========================================
                    // 경로 종류
                    // ========================================

                    String pathCode =
                            j.getPthTypCd();


                    dto.setRouteType(

                            pathCode != null

                                    ? PATH_TYPE_LABELS
                                        .getOrDefault(
                                                pathCode,
                                                pathCode
                                        )

                                    : "-"
                    );


                    // ========================================
                    // 거리
                    // ========================================

                    if (
                            j.getStrtLat() != null
                            &&
                            j.getStrtLng() != null
                            &&
                            j.getEndLat() != null
                            &&
                            j.getEndLng() != null
                    ) {

                        double distanceM =
                                calculateDistance(

                                        j.getStrtLat(),
                                        j.getStrtLng(),

                                        j.getEndLat(),
                                        j.getEndLng()
                                );


                        dto.setDistance(

                                String.format(
                                        "%.1fkm",
                                        distanceM
                                                / 1000.0
                                )
                        );

                    } else {

                        dto.setDistance("-");
                    }


                    return dto;
                })

                .collect(
                        Collectors.toList()
                );
    }


    // ========================================
    // 이번 주 앱을 가장 많이 사용한 친구
    // ========================================

    public TopFriendDto getTopFriend(
            Long memberId
    ) {

        // 이번 주 월요일
        LocalDate monday =
                LocalDate
                        .now()
                        .with(
                                TemporalAdjusters
                                        .previousOrSame(
                                                DayOfWeek.MONDAY
                                        )
                        );


        // 이번 주 월요일 00:00
        LocalDateTime weekStart =
                monday.atStartOfDay();


        // 다음 주 월요일 00:00
        LocalDateTime weekEnd =
                monday
                        .plusWeeks(1)
                        .atStartOfDay();


        List<Object[]> rows =
                jrnyRepository
                        .findTopFriendOfWeek(

                                memberId,
                                weekStart,
                                weekEnd
                        );


        // 친구가 없을 경우
        if (
                rows == null
                ||
                rows.isEmpty()
        ) {

            return null;
        }


        Object[] row =
                rows.get(0);


        Long friendMemberId =
                ((Number) row[0])
                        .longValue();


        String friendName =
                row[1] != null
                        ? row[1].toString()
                        : "-";


        long useCount =
                ((Number) row[2])
                        .longValue();


        return new TopFriendDto(

                friendMemberId,
                friendName,
                useCount
        );
    }


    // ========================================
    // 위도/경도 직선거리 계산
    // ========================================

    private double calculateDistance(

            double lat1,
            double lon1,

            double lat2,
            double lon2
    ) {

        final double EARTH_RADIUS =
                6371000.0;


        double latDistance =
                Math.toRadians(
                        lat2 - lat1
                );


        double lonDistance =
                Math.toRadians(
                        lon2 - lon1
                );


        double a =

                Math.sin(
                        latDistance / 2
                )

                *

                Math.sin(
                        latDistance / 2
                )

                +

                Math.cos(
                        Math.toRadians(
                                lat1
                        )
                )

                *

                Math.cos(
                        Math.toRadians(
                                lat2
                        )
                )

                *

                Math.sin(
                        lonDistance / 2
                )

                *

                Math.sin(
                        lonDistance / 2
                );


        double c =
                2
                *
                Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(
                                1 - a
                        )
                );


        return EARTH_RADIUS * c;
    }
}
