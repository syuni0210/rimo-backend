package com.ansim.backend.service;

import com.ansim.backend.dto.DestinationCreateRequestDto;
import com.ansim.backend.dto.DestinationResponseDto;
import com.ansim.backend.repository.DestinationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinationService {

    private final DestinationRepository destinationRepository;

    public DestinationService(
            DestinationRepository destinationRepository
    ) {
        this.destinationRepository = destinationRepository;
    }

    public List<DestinationResponseDto> getDestinations(
            Long memberId
    ) {

        return destinationRepository.findByMemberId(
                memberId
        );
    }

    public void createDestination(
            Long memberId,
            DestinationCreateRequestDto request
    ) {

        destinationRepository.save(
                memberId,
                request
        );
    }

    public void deleteDestination(
            Long memberId,
            Long destinationId
    ) {

        destinationRepository.delete(
                memberId,
                destinationId
        );
    }
}
