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

        // 1. 카카오 후보 경로 생성
        RouteCandidateDto shortestCandidate =
                createCandidate(
                        request,
                        "SHORTEST"
                );

        RouteCandidateDto broadCandidate =
                createCandidate(
                        request,
                        "BROAD_FIRST"
                );


        // 2. 각 후보 주변 실제 안전시설 조회
        SafetyFacilitySummaryDto shortestFacilities =
                getSafetyFacilities(
                        shortestCandidate
                );

        SafetyFacilitySummaryDto broadFacilities =
                getSafetyFacilities(
                        broadCandidate
                );


        shortestCandidate.setFacilities(
                shortestFacilities
        );

        broadCandidate.setFacilities(
                broadFacilities
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
        return new AiSafeRouteResponseDto(

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
    }


    // ========================================
    // 후보 경로 주변 실제 안전시설 조회
    // ========================================

    private SafetyFacilitySummaryDto getSafetyFacilities(
            RouteCandidateDto candidate
    ) {

        List<RoutePointDto> path =
                candidate.getPath();


        if (
                path == null ||
                path.isEmpty()
        ) {
            return new SafetyFacilitySummaryDto(
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
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


        // 약 50m 여유 영역
        double margin =
                0.0005;


        double swLat =
                minLat - margin;

        double swLng =
                minLng - margin;

        double neLat =
                maxLat + margin;

        double neLng =
                maxLng + margin;


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

        double maxDistanceMeter =
                50.0;


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


        return new SafetyFacilitySummaryDto(

                cctv.size(),

                emergencyBell.size(),

                police.size(),

                safeHouse.size(),

                securityLight.size(),

                smartLight.size()
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
}
