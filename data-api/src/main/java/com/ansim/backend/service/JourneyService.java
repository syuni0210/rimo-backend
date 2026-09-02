package com.ansim.backend.service;

import com.ansim.backend.dto.JourneySaveRequestDto;
import com.ansim.backend.entity.Jrny;
import com.ansim.backend.repository.JrnyRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class JourneyService {

    private final JrnyRepository jrnyRepository;

    public JourneyService(JrnyRepository jrnyRepository) {
        this.jrnyRepository = jrnyRepository;
    }

    public Long saveJourney(JourneySaveRequestDto request) {

        Jrny jrny = new Jrny();

        jrny.setMmbrId(request.getMemberId());
        jrny.setStrtAddrss(request.getStartAddress());
        jrny.setEndAddrss(request.getEndAddress());
        jrny.setStrtLat(request.getStartLatitude());
        jrny.setStrtLng(request.getStartLongitude());
        jrny.setEndLat(request.getEndLatitude());
        jrny.setEndLng(request.getEndLongitude());
        jrny.setPthTypCd(convertPathTypeCode(request.getPathTypeCode()));
        jrny.setSttsCd(
                request.getStatusCode() != null
                        ? request.getStatusCode()
                        : "J002"
        );

        jrny.setStrtDt(
                parseDateTimeOrNow(request.getStartDateTime())
        );

        jrny.setEndDt(
                request.getEndDateTime() != null
                        ? parseDateTimeOrNow(request.getEndDateTime())
                        : LocalDateTime.now()
        );

        Jrny saved = jrnyRepository.save(jrny);

        return saved.getJrnyId();
    }

    private LocalDateTime parseDateTimeOrNow(String value) {

        if (value == null || value.isBlank()) {
            return LocalDateTime.now();
        }

        try {
            return LocalDateTime.parse(
                    value,
                    DateTimeFormatter.ISO_DATE_TIME
            );
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
    private String convertPathTypeCode(String routeMode) {

        if (routeMode == null) {
            return "R001";
        }

        return switch (routeMode) {
            case "SHORTEST" -> "R001";
            case "AI_SAFE" -> "R002";
            case "BROAD_FIRST" -> "R003";
            default -> "R001";
        };
    }
}
