package com.ansim.backend.controller;

import com.ansim.backend.dto.FacilityMapDto;
import com.ansim.backend.dto.RoutePreferenceDto;
import com.ansim.backend.dto.ReturnRecordDto;
import com.ansim.backend.dto.SummaryDto;
import com.ansim.backend.dto.TopFriendDto;
import com.ansim.backend.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    private final ReportService reportService;


    public ReportController(
            ReportService reportService
    ) {
        this.reportService = reportService;
    }

    // ========================================
// 안심지도 - CCTV 조회
// ========================================

    @GetMapping("/cctv")
    public List<FacilityMapDto> getCctv(

            @RequestParam
            double swLat,

            @RequestParam
            double swLng,

            @RequestParam
            double neLat,

            @RequestParam
            double neLng

    ) {

        return reportService
                .getCctvInBounds(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 스마트 가로등 조회
// ========================================

    @GetMapping("/smart-light")
    public List<FacilityMapDto> getSmartLight(

            @RequestParam
            double swLat,

            @RequestParam
            double swLng,

            @RequestParam
            double neLat,

            @RequestParam
            double neLng

    ) {

        return reportService
                .getSmartLightInBounds(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 지킴이집 조회
// ========================================

    @GetMapping("/safe-house")
    public List<FacilityMapDto> getSafeHouse(

            @RequestParam
            double swLat,

            @RequestParam
            double swLng,

            @RequestParam
            double neLat,

            @RequestParam
            double neLng

    ) {

        return reportService
                .getSafeHouseInBounds(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 지구대 / 파출소 조회
// ========================================

    @GetMapping("/police")
    public List<FacilityMapDto> getPolice(

            @RequestParam
            double swLat,

            @RequestParam
            double swLng,

            @RequestParam
            double neLat,

            @RequestParam
            double neLng

    ) {

        return reportService
                .getPoliceInBounds(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 비상벨 조회
// ========================================

    @GetMapping("/emergency-bell")
    public List<FacilityMapDto> getEmergencyBell(

            @RequestParam
            double swLat,

            @RequestParam
            double swLng,

            @RequestParam
            double neLat,

            @RequestParam
            double neLng

    ) {

        return reportService
                .getEmergencyBellInBounds(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );
    }


// ========================================
// 안심지도 - 보안등 조회
// ========================================

    @GetMapping("/security-light")
    public List<FacilityMapDto> getSecurityLight(

            @RequestParam
            double swLat,

            @RequestParam
            double swLng,

            @RequestParam
            double neLat,

            @RequestParam
            double neLng

    ) {

        return reportService
                .getSecurityLightInBounds(
                        swLat,
                        swLng,
                        neLat,
                        neLng
                );
    }

    // ========================================
    // 요약
    // ========================================

    @GetMapping("/summary")
    public SummaryDto getSummary(
            @RequestParam
            Long memberId
    ) {

        return reportService
                .getSummary(
                        memberId
                );
    }


    // ========================================
    // 경로 선호도
    // ========================================

    @GetMapping("/route-preference")
    public List<RoutePreferenceDto>
    getRoutePreference(

            @RequestParam
            Long memberId
    ) {

        return reportService
                .getRoutePreference(
                        memberId
                );
    }


    // ========================================
    // 귀가 기록
    // ========================================

    @GetMapping("/records")
    public List<ReturnRecordDto>
    getRecords(

            @RequestParam
            Long memberId,

            @RequestParam(
                    required = false
            )
            String date
    ) {

        return reportService
                .getRecords(
                        memberId,
                        date
                );
    }


    // ========================================
    // 이번 주 앱을 가장 많이 사용한 친구
    // ========================================

    @GetMapping("/top-friend")
    public TopFriendDto getTopFriend(

            @RequestParam
            Long memberId
    ) {

        return reportService
                .getTopFriend(
                        memberId
                );
    }
}
