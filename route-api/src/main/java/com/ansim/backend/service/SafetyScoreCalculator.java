package com.ansim.backend.service;

import com.ansim.backend.dto.SafetyFacilitySummaryDto;
import org.springframework.stereotype.Component;

@Component
public class SafetyScoreCalculator {

    public double calculate(
            SafetyFacilitySummaryDto facility,
            int distanceMeter
    ) {

        if (
                facility == null ||
                distanceMeter <= 0
        ) {
            return 0.0;
        }


        // ========================================
        // 1. 경로 길이를 km 단위로 변환
        // ========================================

        double distanceKm =
                distanceMeter / 1000.0;


        // ========================================
        // 2. 경로 1km당 시설 밀도 계산
        //
        // 예)
        // CCTV 40개 / 2km
        // = 1km당 CCTV 20개
        // ========================================

        double cctvDensity =
                safeCount(
                        facility.getCctvCount()
                )
                        / distanceKm;

        double emergencyBellDensity =
                safeCount(
                        facility.getEmergencyBellCount()
                )
                        / distanceKm;

        double policeDensity =
                safeCount(
                        facility.getPoliceCount()
                )
                        / distanceKm;

        double safeHouseDensity =
                safeCount(
                        facility.getSafeHouseCount()
                )
                        / distanceKm;

        double securityLightDensity =
                safeCount(
                        facility.getSecurityLightCount()
                )
                        / distanceKm;

        double smartLightDensity =
                safeCount(
                        facility.getSmartLightCount()
                )
                        / distanceKm;


        // ========================================
        // 3. 시설별 점수 정규화
        //
        // normalizeDensity(
        //     실제 1km당 시설 개수,
        //     기준 밀도
        // )
        //
        // 기준 밀도 이상이면 해당 항목은 100점
        //
        // 아래 기준값은 현재 프로젝트용 초기값이며
        // 실제 데이터 분포를 보고 추후 조정 가능
        // ========================================

        double cctvScore =
                normalizeDensity(
                        cctvDensity,
                        40.0
                );

        double emergencyBellScore =
                normalizeDensity(
                        emergencyBellDensity,
                        5.0
                );

        double policeScore =
                normalizeDensity(
                        policeDensity,
                        1.5
                );

        double safeHouseScore =
                normalizeDensity(
                        safeHouseDensity,
                        4.0
                );

        double securityLightScore =
                normalizeDensity(
                        securityLightDensity,
                        60.0
                );

        double smartLightScore =
                normalizeDensity(
                        smartLightDensity,
                        15.0
                );


        // ========================================
        // 4. 시설별 가중치
        //
        // CCTV              25%
        // 비상벨             20%
        // 경찰시설           15%
        // 안심지킴이집        15%
        // 보안등             15%
        // 스마트가로등        10%
        //
        // 합계 = 100%
        // ========================================

        double finalScore =

                cctvScore
                        * 0.25

                        +

                emergencyBellScore
                        * 0.20

                        +

                policeScore
                        * 0.15

                        +

                safeHouseScore
                        * 0.15

                        +

                securityLightScore
                        * 0.15

                        +

                smartLightScore
                        * 0.10;


        // ========================================
        // 5. 0 ~ 100 범위 보정
        // ========================================

        finalScore =
                Math.max(
                        0.0,
                        Math.min(
                                100.0,
                                finalScore
                        )
                );


        // 소수점 첫째 자리까지 반환
        return Math.round(
                finalScore * 10.0
        )
                / 10.0;
    }


    // ========================================
    // null 안전 처리
    // ========================================

    private int safeCount(
            Integer count
    ) {

        if (count == null) {
            return 0;
        }

        return Math.max(
                count,
                0
        );
    }


    // ========================================
    // 시설 밀도를 0 ~ 100점으로 변환
    //
    // 예)
    // 실제 밀도 20
    // 기준 밀도 40
    //
    // 20 / 40 * 100
    // = 50점
    //
    // 기준 밀도를 넘으면 최대 100점
    // ========================================

    private double normalizeDensity(
            double density,
            double targetDensity
    ) {

        if (
                density <= 0 ||
                targetDensity <= 0
        ) {
            return 0.0;
        }


        double score =
                (
                        density
                                /
                                targetDensity
                )
                        * 100.0;


        return Math.min(
                score,
                100.0
        );
    }
}
