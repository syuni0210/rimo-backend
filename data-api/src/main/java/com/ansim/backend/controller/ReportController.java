package com.ansim.backend.controller;

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
