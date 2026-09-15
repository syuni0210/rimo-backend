package com.ansim.backend.client;

import com.ansim.backend.dto.KakaoWalkingRouteResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class KakaoRouteClient {

    private final RestClient restClient;

    private final String restApiKey;

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;


    public KakaoRouteClient(
            RestClient.Builder restClientBuilder,
            @Value("${kakao.mobility.rest-api-key}")
            String restApiKey,
            StringRedisTemplate redisTemplate,
            ObjectMapper objectMapper
    ) {

        this.restClient =
                restClientBuilder
                        .baseUrl(
                                "https://dapi.kakao.com"
                        )
                        .build();

        this.restApiKey =
                restApiKey;

        this.redisTemplate =
                redisTemplate;

        this.objectMapper =
                objectMapper;
    }

    @Timed(value = "kakao.route.api.duration", description = "카카오 도보경로 API 호출 시간")
    public KakaoWalkingRouteResponseDto getWalkingRoute(

            Double startLatitude,
            Double startLongitude,

            Double destinationLatitude,
            Double destinationLongitude,

            String routeMode
    ) {

        String cacheKey = String.format(
                "route_cache:%.5f:%.5f:%.5f:%.5f:%s",
                startLatitude, startLongitude,
                destinationLatitude, destinationLongitude,
                routeMode
        );

        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return objectMapper.readValue(cached, KakaoWalkingRouteResponseDto.class);
            }
        } catch (Exception e) {
            // 캐시 읽기 실패는 무시하고 카카오 API 호출로 진행
        }

        KakaoWalkingRouteResponseDto response = restClient
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

        try {
            String json = objectMapper.writeValueAsString(response);
            redisTemplate.opsForValue().set(cacheKey, json, Duration.ofHours(24));
        } catch (Exception e) {
            // 캐시 저장 실패해도 응답은 정상 반환
        }

        return response;
    }
}
