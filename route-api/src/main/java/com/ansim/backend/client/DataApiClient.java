package com.ansim.backend.client;

import com.ansim.backend.dto.FacilityMapDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class DataApiClient {

    private final RestClient restClient;

    public DataApiClient(
            RestClient.Builder builder,
            @Value("${data-api.base-url}")
            String baseUrl
    ) {

        this.restClient =
                builder
                        .baseUrl(baseUrl)
                        .build();
    }


    public List<FacilityMapDto> getCctv(
            double swLat,
            double swLng,
            double neLat,
            double neLng
    ) {

        return getFacilities(
                "/api/report/cctv",
                swLat,
                swLng,
                neLat,
                neLng
        );
    }


    public List<FacilityMapDto> getEmergencyBell(
            double swLat,
            double swLng,
            double neLat,
            double neLng
    ) {

        return getFacilities(
                "/api/report/emergency-bell",
                swLat,
                swLng,
                neLat,
                neLng
        );
    }


    public List<FacilityMapDto> getPolice(
            double swLat,
            double swLng,
            double neLat,
            double neLng
    ) {

        return getFacilities(
                "/api/report/police",
                swLat,
                swLng,
                neLat,
                neLng
        );
    }


    public List<FacilityMapDto> getSafeHouse(
            double swLat,
            double swLng,
            double neLat,
            double neLng
    ) {

        return getFacilities(
                "/api/report/safe-house",
                swLat,
                swLng,
                neLat,
                neLng
        );
    }


    public List<FacilityMapDto> getSecurityLight(
            double swLat,
            double swLng,
            double neLat,
            double neLng
    ) {

        return getFacilities(
                "/api/report/security-light",
                swLat,
                swLng,
                neLat,
                neLng
        );
    }


    public List<FacilityMapDto> getSmartLight(
            double swLat,
            double swLng,
            double neLat,
            double neLng
    ) {

        return getFacilities(
                "/api/report/smart-light",
                swLat,
                swLng,
                neLat,
                neLng
        );
    }


    private List<FacilityMapDto> getFacilities(
            String path,
            double swLat,
            double swLng,
            double neLat,
            double neLng
    ) {

        List<FacilityMapDto> result =
                restClient
                        .get()
                        .uri(
                                uriBuilder ->
                                        uriBuilder
                                                .path(path)
                                                .queryParam(
                                                        "swLat",
                                                        swLat
                                                )
                                                .queryParam(
                                                        "swLng",
                                                        swLng
                                                )
                                                .queryParam(
                                                        "neLat",
                                                        neLat
                                                )
                                                .queryParam(
                                                        "neLng",
                                                        neLng
                                                )
                                                .build()
                        )
                        .retrieve()
                        .body(
                                new ParameterizedTypeReference<
                                        List<FacilityMapDto>
                                        >() {
                                }
                        );


        return result != null
                ? result
                : List.of();
    }
}
