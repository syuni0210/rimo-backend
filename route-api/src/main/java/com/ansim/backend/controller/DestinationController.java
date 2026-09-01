package com.ansim.backend.controller;

import com.ansim.backend.dto.DestinationCreateRequestDto;
import com.ansim.backend.dto.DestinationResponseDto;
import com.ansim.backend.service.DestinationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/route")
public class DestinationController {

    private final DestinationService destinationService;

    public DestinationController(
            DestinationService destinationService
    ) {
        this.destinationService = destinationService;
    }

    // 기본 목적지 조회
    @GetMapping("/{memberId}/destinations")
    public ResponseEntity<List<DestinationResponseDto>>
    getDestinations(
            @PathVariable Long memberId
    ) {

        return ResponseEntity.ok(
                destinationService.getDestinations(
                        memberId
                )
        );
    }

    // 기본 목적지 등록
    @PostMapping("/{memberId}/destinations")
    public ResponseEntity<Void> createDestination(
            @PathVariable Long memberId,
            @RequestBody DestinationCreateRequestDto request
    ) {

        destinationService.createDestination(
                memberId,
                request
        );

        return ResponseEntity.ok().build();
    }

    // 기본 목적지 삭제
    @DeleteMapping(
            "/{memberId}/destinations/{destinationId}"
    )
    public ResponseEntity<Void> deleteDestination(
            @PathVariable Long memberId,
            @PathVariable Long destinationId
    ) {

        destinationService.deleteDestination(
                memberId,
                destinationId
        );

        return ResponseEntity.noContent().build();
    }
}
