package com.ansim.backend.client;

import com.ansim.backend.dto.KakaoWalkingRouteResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KakaoRouteClient {

    private final RestClient restClient;

    private final String restApiKey;


    public KakaoRouteClient(
            RestClient.Builder restClientBuilder,
            @Value("${kakao.mobility.rest-api-key}")
            String restApiKey
    ) {

        this.restClient =
                restClientBuilder
                        .baseUrl(
                                "https://dapi.kakao.com"
                        )
                        .build();

        this.restApiKey =
                restApiKey;
    }


    public KakaoWalkingRouteResponseDto getWalkingRoute(

            Double startLatitude,
            Double startLongitude,

            Double destinationLatitude,
            Double destinationLongitude,

            String routeMode
    ) {

        return restClient
                .get()
                .uri(
                        uriBuilder ->

                                uriBuilder
                                        .path(
                                                "/v2/routing/walk"
                                        )

                                        .queryParam(
                                                "start_x",
                                                startLongitude
                                        )

                                        .queryParam(
                                                "start_y",
                                                startLatitude
                                        )

                                        .queryParam(
                                                "end_x",
                                                destinationLongitude
                                        )

                                        .queryParam(
                                                "end_y",
                                                destinationLatitude
                                        )

                                        .queryParam(
                                                "s_name",
                                                "현재 위치"
                                        )

                                        .queryParam(
                                                "e_name",
                                                "목적지"
                                        )

                                        .queryParam(
                                                "input_coord",
                                                "WGS84"
                                        )

                                        .queryParam(
                                                "output_coord",
                                                "WGS84"
                                        )

                                        .queryParam(
                                                "route_mode",
                                                routeMode
                                        )

                                        .build()
                )

                .header(
                        "Authorization",
                        "KakaoAK " + restApiKey
                )

                .retrieve()

                .body(
                        KakaoWalkingRouteResponseDto.class
                );
    }
}
