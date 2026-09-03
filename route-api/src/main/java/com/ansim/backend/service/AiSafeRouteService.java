package com.ansim.backend.service;

import com.ansim.backend.client.DataApiClient;
import com.ansim.backend.client.GeminiRecommendationClient;
import com.ansim.backend.client.KakaoRouteClient;
import com.ansim.backend.dto.AiSafeRouteRequestDto;
import com.ansim.backend.dto.AiSafeRouteResponseDto;
import com.ansim.backend.dto.FacilityMapDto;
import com.ansim.backend.dto.KakaoWalkingRouteResponseDto;
import com.ansim.backend.dto.RouteCandidateDto;
import com.ansim.backend.dto.RoutePointDto;
import com.ansim.backend.dto.SafetyFacilitySummaryDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AiSafeRouteService {

    private final KakaoRouteClient kakaoRouteClient;
    private final DataApiClient dataApiClient;
    private final SafetyScoreCalculator safetyScoreCalculator;
    private final GeminiRecommendationClient geminiRecommendationClient;


    public AiSafeRouteService(
            KakaoRouteClient kakaoRouteClient,
            DataApiClient dataApiClient,
            SafetyScoreCalculator safetyScoreCalculator,
            GeminiRecommendationClient geminiRecommendationClient
    ) {
        this.kakaoRouteClient = kakaoRouteClient;
        this.dataApiClient = dataApiClient;
        this.safetyScoreCalculator = safetyScoreCalculator;
        this.geminiRecommendationClient = geminiRecommendationClient;
    }


    public AiSafeRouteResponseDto findAiSafeRoute(
            AiSafeRouteRequestDto request
    ) {

        // ========================================
// 1. 후보 경로 준비
//
// Android에서 이미 계산한 Kakao 경로가 전달되면
// 해당 경로를 그대로 사용합니다.
//
// 후보 경로가 전달되지 않은 기존 요청의 경우에는
// 이전 방식대로 Backend가 Kakao API를 호출합니다.
// ========================================

        RouteCandidateDto shortestCandidate;

        if (
                request.getShortestCandidate() != null &&
                        request.getShortestCandidate().getDistanceMeter() != null &&
                        request.getShortestCandidate().getTimeSecond() != null &&
                        request.getShortestCandidate().getPath() != null &&
                        request.getShortestCandidate().getPath().size() >= 2
        ) {

            shortestCandidate =
                    new RouteCandidateDto(
                            "SHORTEST",
                            request.getShortestCandidate().getDistanceMeter(),
                            request.getShortestCandidate().getTimeSecond(),
                            request.getShortestCandidate().getPath()
                    );

        } else {

            // 기존 Android 요청과의 호환용 fallback
            shortestCandidate =
                    createCandidate(
                            request,
                            "SHORTEST"
                    );
        }


        RouteCandidateDto broadCandidate;

        if (
                request.getBroadCandidate() != null &&
                        request.getBroadCandidate().getDistanceMeter() != null &&
                        request.getBroadCandidate().getTimeSecond() != null &&
                        request.getBroadCandidate().getPath() != null &&
                        request.getBroadCandidate().getPath().size() >= 2
        ) {

            broadCandidate =
                    new RouteCandidateDto(
                            "BROAD_FIRST",
                            request.getBroadCandidate().getDistanceMeter(),
                            request.getBroadCandidate().getTimeSecond(),
                            request.getBroadCandidate().getPath()
                    );

        } else {

            // 기존 Android 요청과의 호환용 fallback
            broadCandidate =
                    createCandidate(
                            request,
                            "BROAD_FIRST"
                    );
        }


        // 2. 각 후보 주변 실제 안전시설 조회
        SafetyFacilityResult shortestFacilityResult =
        getSafetyFacilities(
                shortestCandidate
        );

        SafetyFacilityResult broadFacilityResult =
        getSafetyFacilities(
                broadCandidate
        );

        SafetyFacilitySummaryDto shortestFacilities =
        shortestFacilityResult.getSummary();

        SafetyFacilitySummaryDto broadFacilities =
        broadFacilityResult.getSummary();


        shortestCandidate.setFacilities(
                shortestFacilities
        );

        broadCandidate.setFacilities(
                broadFacilities
        );

        shortestCandidate.setMapFacilities(
                shortestFacilityResult.getFacilities()
        );

        broadCandidate.setMapFacilities(
                broadFacilityResult.getFacilities()
        );


        // 3. 안전점수 계산
        double shortestScore =
                safetyScoreCalculator.calculate(
                        shortestFacilities,
                        shortestCandidate.getDistanceMeter()
                );

        double broadScore =
                safetyScoreCalculator.calculate(
                        broadFacilities,
                        broadCandidate.getDistanceMeter()
                );


        shortestCandidate.setSafetyScore(
                shortestScore
        );

        broadCandidate.setSafetyScore(
                broadScore
        );


        // 4. 안전점수가 높은 후보를 최종 경로로 선택
        RouteCandidateDto selectedCandidate;

        if (
                broadCandidate.getSafetyScore()
                        >
                        shortestCandidate.getSafetyScore()
        ) {
            selectedCandidate =
                    broadCandidate;
        } else {
            selectedCandidate =
                    shortestCandidate;
        }


        // 5. 후보 목록
        List<RouteCandidateDto> candidates =
                List.of(
                        shortestCandidate,
                        broadCandidate
                );


        SafetyFacilitySummaryDto selectedFacilities =
                selectedCandidate.getFacilities();

        List<FacilityMapDto> selectedFacilityList =
                selectedCandidate.getMapFacilities();

        // ========================================
        // 6. Gemini API를 이용해 추천 이유 생성
        //
        // 최종 경로 선택 자체는 서버의 안전점수로 결정하고
        // Gemini는 선택된 이유를 사용자에게 설명
        // ========================================

        String recommendationReason =
                geminiRecommendationClient
                        .generateRecommendationReason(
                                selectedCandidate,
                                candidates
                        );


        // 7. 최종 AI 안전경로 응답
        AiSafeRouteResponseDto response =
        new AiSafeRouteResponseDto(

                "AI_SAFE",

                selectedCandidate.getDistanceMeter(),

                selectedCandidate.getTimeSecond(),

                selectedCandidate.getSafetyScore(),

                selectedFacilities.getCctvCount(),

                selectedFacilities.getEmergencyBellCount(),

                selectedFacilities.getPoliceCount(),

                selectedFacilities.getSafeHouseCount(),

                selectedFacilities.getSecurityLightCount(),

                selectedFacilities.getSmartLightCount(),

                recommendationReason,

                selectedCandidate.getPath(),

                candidates
        );

response.setFacilities(
        selectedFacilityList
);

return response;
    }

    // ========================================
// 사용자가 실제로 선택한 경로 주변 안전시설 조회
//
// AI 계산이 아직 끝나지 않았는데
// SHORTEST 또는 BROAD_FIRST를 먼저 선택한 경우 사용합니다.
//
// 기존 AI 안전경로에서 사용 중인
// getSafetyFacilities()를 그대로 재사용하므로
// 별도의 50m 계산 로직을 만들지 않습니다.
// ========================================
    public List<FacilityMapDto> getFacilitiesNearPath(
            List<RoutePointDto> path
    ) {

        // 경로를 계산할 수 없는 경우
        if (
                path == null ||
                        path.size() < 2
        ) {
            return List.of();
        }

        // 기존 시설 검색 로직이 RouteCandidateDto를 받으므로
        // 실제 선택 경로를 임시 candidate에 넣습니다.
        RouteCandidateDto candidate =
                new RouteCandidateDto();

        candidate.setPath(path);

        // 기존 AI 경로 시설 검색 로직 재사용
        SafetyFacilityResult result =
                getSafetyFacilities(candidate);

        return result.getFacilities();
    }

    // ========================================
    // 현재 사용자 위치 주변 50m 안전시설 조회
    //
    // 귀가 진행 중에는 전체 경로 주변 시설이 아니라
    // 사용자의 현재 GPS 위치를 기준으로 조회합니다.
    //
    // 1. 현재 위치를 중심으로 약 50m Bounding Box 조회
    // 2. Haversine 거리 계산으로 실제 50m 이내만 필터
    // ========================================

    public List<FacilityMapDto> getFacilitiesNearLocation(
            double latitude,
            double longitude
    ) {

        final double maxDistanceMeter =
                50.0;


        // 위도 1도 ≒ 111.32km
        double latMargin =
                maxDistanceMeter / 111320.0;


        // 경도는 위도에 따라 실제 거리가 달라지므로 보정
        double lngMargin =
                maxDistanceMeter
                        / (
                        111320.0
                                * Math.cos(
                                Math.toRadians(latitude)
                        )
                );


        double swLat =
                latitude - latMargin;

        double swLng =
                longitude - lngMargin;

        double neLat =
                latitude + latMargin;

        double neLng =
                longitude + lngMargin;


        // ========================================
        // data-api에서 현재 위치 주변 시설 조회
        // ========================================

        List<FacilityMapDto> cctv =
                dataApiClient.getCctv(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> emergencyBell =
                dataApiClient.getEmergencyBell(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> police =
                dataApiClient.getPolice(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> safeHouse =
                dataApiClient.getSafeHouse(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> securityLight =
                dataApiClient.getSecurityLight(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> smartLight =
                dataApiClient.getSmartLight(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );


        // Bounding Box 안에 있더라도
        // 원형 반경 50m 밖일 수 있으므로 실제 거리로 다시 필터합니다.
        List<FacilityMapDto> facilities =
                new ArrayList<>();


        facilities.addAll(
                filterFacilitiesNearLocation(
                        cctv,
                        latitude,
                        longitude,
                        maxDistanceMeter
                )
        );

        facilities.addAll(
                filterFacilitiesNearLocation(
                        emergencyBell,
                        latitude,
                        longitude,
                        maxDistanceMeter
                )
        );

        facilities.addAll(
                filterFacilitiesNearLocation(
                        police,
                        latitude,
                        longitude,
                        maxDistanceMeter
                )
        );

        facilities.addAll(
                filterFacilitiesNearLocation(
                        safeHouse,
                        latitude,
                        longitude,
                        maxDistanceMeter
                )
        );

        facilities.addAll(
                filterFacilitiesNearLocation(
                        securityLight,
                        latitude,
                        longitude,
                        maxDistanceMeter
                )
        );

        facilities.addAll(
                filterFacilitiesNearLocation(
                        smartLight,
                        latitude,
                        longitude,
                        maxDistanceMeter
                )
        );


        return facilities;
    }

    // ========================================
    // 후보 경로 주변 실제 안전시설 조회
    // ========================================

    private SafetyFacilityResult getSafetyFacilities(
        RouteCandidateDto candidate
    ) {

        List<RoutePointDto> path =
                candidate.getPath();


        if (
                path == null ||
                path.isEmpty()
        ) {
            return new SafetyFacilityResult(
        new SafetyFacilitySummaryDto(
                0,
                0,
                0,
                0,
                0,
                0
        ),
        List.of()
);
        }


        // ========================================
        // 경로 전체 Bounding Box 계산
        // ========================================

        double minLat =
                Double.MAX_VALUE;

        double minLng =
                Double.MAX_VALUE;

        double maxLat =
                -Double.MAX_VALUE;

        double maxLng =
                -Double.MAX_VALUE;


        for (RoutePointDto point : path) {

            double lat =
                    point.getLatitude();

            double lng =
                    point.getLongitude();


            if (lat < minLat) {
                minLat = lat;
            }

            if (lat > maxLat) {
                maxLat = lat;
            }

            if (lng < minLng) {
                minLng = lng;
            }

            if (lng > maxLng) {
                maxLng = lng;
            }
        }


        // 실제 약 50m 여유 영역
        double maxDistanceMeter =
                50.0;

        double midLat =
                (minLat + maxLat) / 2.0;

        double latMargin =
                maxDistanceMeter / 111320.0;

        double lngMargin =
                maxDistanceMeter
                        / (
                                111320.0
                                * Math.cos(
                                        Math.toRadians(midLat)
                                )
                        );


        double swLat =
                minLat - latMargin;

        double swLng =
                minLng - lngMargin;

        double neLat =
                maxLat + latMargin;

        double neLng =
                maxLng + lngMargin;


        // ========================================
        // data-api에서 시설 조회
        // ========================================

        List<FacilityMapDto> cctv =
                dataApiClient.getCctv(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> emergencyBell =
                dataApiClient.getEmergencyBell(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> police =
                dataApiClient.getPolice(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> safeHouse =
                dataApiClient.getSafeHouse(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> securityLight =
                dataApiClient.getSecurityLight(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );

        List<FacilityMapDto> smartLight =
                dataApiClient.getSmartLight(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );


        // ========================================
        // 실제 경로에서 50m 이내 시설만 필터
        // ========================================

        cctv =
                filterFacilitiesNearPath(
                        cctv,
                        path,
                        maxDistanceMeter
                );

        emergencyBell =
                filterFacilitiesNearPath(
                        emergencyBell,
                        path,
                        maxDistanceMeter
                );

        police =
                filterFacilitiesNearPath(
                        police,
                        path,
                        maxDistanceMeter
                );

        safeHouse =
                filterFacilitiesNearPath(
                        safeHouse,
                        path,
                        maxDistanceMeter
                );

        securityLight =
                filterFacilitiesNearPath(
                        securityLight,
                        path,
                        maxDistanceMeter
                );

        smartLight =
                filterFacilitiesNearPath(
                        smartLight,
                        path,
                        maxDistanceMeter
                );


        List<FacilityMapDto> facilities =
        new ArrayList<>();

facilities.addAll(cctv);
facilities.addAll(emergencyBell);
facilities.addAll(police);
facilities.addAll(safeHouse);
facilities.addAll(securityLight);
facilities.addAll(smartLight);


SafetyFacilitySummaryDto summary =
        new SafetyFacilitySummaryDto(

                cctv.size(),

                emergencyBell.size(),

                police.size(),

                safeHouse.size(),

                securityLight.size(),

                smartLight.size()
        );


return new SafetyFacilityResult(
        summary,
        facilities
);
    }


    // ========================================
    // 실제 경로 주변 시설 필터
    // ========================================

    private List<FacilityMapDto> filterFacilitiesNearPath(
            List<FacilityMapDto> facilities,
            List<RoutePointDto> path,
            double maxDistanceMeter
    ) {

        List<FacilityMapDto> filtered =
                new ArrayList<>();


        if (
                facilities == null ||
                facilities.isEmpty()
        ) {
            return filtered;
        }


        for (FacilityMapDto facility : facilities) {

            if (
                    facility.getLat() == null ||
                    facility.getLng() == null
            ) {
                continue;
            }


            for (RoutePointDto point : path) {

                double distance =
                        calculateDistanceMeter(
                                facility.getLat(),
                                facility.getLng(),
                                point.getLatitude(),
                                point.getLongitude()
                        );


                if (
                        distance <= maxDistanceMeter
                ) {

                    filtered.add(
                            facility
                    );

                    break;
                }
            }
        }


        return filtered;
    }

    // ========================================
    // 현재 위치 기준 실제 반경 시설 필터
    // ========================================

    private List<FacilityMapDto> filterFacilitiesNearLocation(
            List<FacilityMapDto> facilities,
            double latitude,
            double longitude,
            double maxDistanceMeter
    ) {

        List<FacilityMapDto> filtered =
                new ArrayList<>();


        if (
                facilities == null ||
                        facilities.isEmpty()
        ) {
            return filtered;
        }


        for (FacilityMapDto facility : facilities) {

            if (
                    facility.getLat() == null ||
                            facility.getLng() == null
            ) {
                continue;
            }


            double distance =
                    calculateDistanceMeter(
                            facility.getLat(),
                            facility.getLng(),
                            latitude,
                            longitude
                    );


            if (
                    distance <= maxDistanceMeter
            ) {

                filtered.add(
                        facility
                );
            }
        }


        return filtered;
    }

    // ========================================
    // 위경도 거리 계산
    // Haversine 공식
    // ========================================

    private double calculateDistanceMeter(
            double lat1,
            double lng1,
            double lat2,
            double lng2
    ) {

        final double earthRadius =
                6371000.0;


        double latDistance =
                Math.toRadians(
                        lat2 - lat1
                );

        double lngDistance =
                Math.toRadians(
                        lng2 - lng1
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
                                lngDistance / 2
                        )

                        *
                        Math.sin(
                                lngDistance / 2
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


        return earthRadius * c;
    }


    // ========================================
    // Kakao 후보 경로 생성
    // ========================================

    private RouteCandidateDto createCandidate(
            AiSafeRouteRequestDto request,
            String routeMode
    ) {

        KakaoWalkingRouteResponseDto kakaoResponse =
                kakaoRouteClient.getWalkingRoute(

                        request.getStartLatitude(),

                        request.getStartLongitude(),

                        request.getDestinationLatitude(),

                        request.getDestinationLongitude(),

                        routeMode
                );


        if (
                kakaoResponse == null ||
                !"OK".equals(
                        kakaoResponse.getStatus()
                ) ||
                kakaoResponse.getRoute() == null
        ) {

            throw new IllegalStateException(
                    "카카오 도보 경로를 찾지 못했습니다. routeMode="
                            + routeMode
            );
        }


        KakaoWalkingRouteResponseDto.WalkingRoute route =
                kakaoResponse.getRoute();


        KakaoWalkingRouteResponseDto.WalkingRouteProperties properties =
                route.getProperties();


        if (
                properties == null ||
                properties.getTotalDistance() == null ||
                properties.getTotalTime() == null
        ) {

            throw new IllegalStateException(
                    "카카오 경로 요약 정보가 없습니다. routeMode="
                            + routeMode
            );
        }


        List<RoutePointDto> routePoints =
                extractRoutePoints(
                        route
                );


        return new RouteCandidateDto(

                routeMode,

                properties.getTotalDistance(),

                properties.getTotalTime(),

                routePoints
        );
    }


    // ========================================
    // Kakao path 좌표 추출
    // + 연속 중복 제거
    // ========================================

    private List<RoutePointDto> extractRoutePoints(
            KakaoWalkingRouteResponseDto.WalkingRoute route
    ) {

        List<RoutePointDto> routePoints =
                new ArrayList<>();


        if (
                route.getLegs() == null
        ) {
            return routePoints;
        }


        Double previousLatitude =
                null;

        Double previousLongitude =
                null;


        for (
                KakaoWalkingRouteResponseDto.WalkingLeg leg
                : route.getLegs()
        ) {

            if (
                    leg.getSteps() == null
            ) {
                continue;
            }


            for (
                    KakaoWalkingRouteResponseDto.WalkingStep step
                    : leg.getSteps()
            ) {

                if (
                        step.getPath() == null ||
                        step.getPath().getPoints() == null
                ) {
                    continue;
                }


                for (
                        List<Double> point
                        : step.getPath().getPoints()
                ) {

                    if (
                            point == null ||
                            point.size() < 2
                    ) {
                        continue;
                    }


                    Double longitude =
                            point.get(0);

                    Double latitude =
                            point.get(1);


                    if (
                            previousLatitude != null &&
                            previousLongitude != null &&
                            Double.compare(
                                    previousLatitude,
                                    latitude
                            ) == 0 &&
                            Double.compare(
                                    previousLongitude,
                                    longitude
                            ) == 0
                    ) {
                        continue;
                    }


                    routePoints.add(
                            new RoutePointDto(
                                    latitude,
                                    longitude
                            )
                    );


                    previousLatitude =
                            latitude;

                    previousLongitude =
                            longitude;
                }
            }
        }


                return routePoints;
    }


    // ========================================
    // 안전시설 조회 결과
    // - summary: 안전점수 계산용 시설 개수
    // - facilities: 지도 마커 표시용 실제 시설 목록
    // ========================================

    private static class SafetyFacilityResult {

        private final SafetyFacilitySummaryDto summary;
        private final List<FacilityMapDto> facilities;


        public SafetyFacilityResult(
                SafetyFacilitySummaryDto summary,
                List<FacilityMapDto> facilities
        ) {
            this.summary = summary;
            this.facilities = facilities;
        }


        public SafetyFacilitySummaryDto getSummary() {
            return summary;
        }


        public List<FacilityMapDto> getFacilities() {
            return facilities;
        }
    }
}
