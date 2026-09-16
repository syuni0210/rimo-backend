package com.ansim.backend.client;

import com.ansim.backend.dto.RouteCandidateDto;
import com.ansim.backend.dto.SafetyFacilitySummaryDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import io.micrometer.core.annotation.Timed;

@Component
public class GeminiRecommendationClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String model;
    private final StringRedisTemplate redisTemplate;

    public GeminiRecommendationClient(
            RestClient.Builder builder,
            @Value("${gemini.api-key}") String apiKey,
            @Value("${gemini.model}") String model,
            StringRedisTemplate redisTemplate
    ) {
        this.restClient = builder
                .baseUrl("https://generativelanguage.googleapis.com")
                .build();

        this.apiKey = apiKey;
        this.model = model;
        this.redisTemplate = redisTemplate;
    }

    @Timed(value = "gemini.recommendation.duration", description = "Gemini AI 추천 문구 생성 시간")
    public String generateRecommendationReason(
            RouteCandidateDto selectedCandidate,
            List<RouteCandidateDto> candidates
    ) {

        // ========================================
        // 캐시 키 생성 (선택된 경로의 특성 기준)
        // ========================================

        String cacheKey =
                buildCacheKey(
                        selectedCandidate
                );

        // ========================================
        // 캐시 확인 (있으면 Gemini 호출 없이 즉시 반환)
        // ========================================

        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                return cached;
            }
        } catch (Exception e) {
            // 캐시 읽기 실패는 무시하고 Gemini 호출로 진행
        }

        String prompt =
                buildPrompt(
                        selectedCandidate,
                        candidates
                );

        Map<String, Object> requestBody =
                Map.of(
                        "contents",
                        List.of(
                                Map.of(
                                        "parts",
                                        List.of(
                                                Map.of(
                                                        "text",
                                                        prompt
                                                )
                                        )
                                )
                        )
                );

        String reason;

        try {

            Map response =
                    restClient
                            .post()
                            .uri(
                                    "/v1beta/models/"
                                            + model
                                            + ":generateContent"
                            )
                            .header(
                                    "x-goog-api-key",
                                    apiKey
                            )
                            .header(
                                    "Content-Type",
                                    "application/json"
                            )
                            .body(requestBody)
                            .retrieve()
                            .body(Map.class);

            String result =
                    extractText(response);

            if (
                    result == null ||
                    result.isBlank()
            ) {
                reason = createFallbackReason(
                        selectedCandidate
                );
            } else {
                reason = result.trim();
            }

        } catch (Exception e) {

            System.err.println(
                    "Gemini API 호출 실패: "
                            + e.getMessage()
            );

            reason = createFallbackReason(
                    selectedCandidate
            );
        }

        // ========================================
        // 결과를 캐시에 저장 (TTL 24시간)
        // ========================================

        try {
            redisTemplate.opsForValue().set(cacheKey, reason, Duration.ofHours(24));
        } catch (Exception e) {
            // 캐시 저장 실패해도 응답은 정상 반환
        }

        return reason;
    }

    private String buildCacheKey(RouteCandidateDto selectedCandidate) {
        SafetyFacilitySummaryDto f = selectedCandidate.getFacilities();

        return String.format(
                "gemini_cache:%s:%d:%d:%.1f:%d:%d:%d:%d:%d:%d",
                selectedCandidate.getRouteMode(),
                selectedCandidate.getDistanceMeter(),
                selectedCandidate.getTimeSecond(),
                selectedCandidate.getSafetyScore(),
                f.getCctvCount(),
                f.getEmergencyBellCount(),
                f.getPoliceCount(),
                f.getSafeHouseCount(),
                f.getSecurityLightCount(),
                f.getSmartLightCount()
        );
    }

    private String buildPrompt(
            RouteCandidateDto selectedCandidate,
            List<RouteCandidateDto> candidates
    ) {

        StringBuilder builder =
                new StringBuilder();

        builder.append(
                """
                너는 야간 보행 안전경로 서비스의 추천 이유를 설명하는 AI이다.

                서버에서 실제 안전시설 데이터와 안전점수를 이용해
                최종 경로를 이미 선택했다.

                경로를 다시 선택하지 말고,
                선택된 경로가 다른 후보보다 왜 추천되는지만 설명해라.

                규칙:
                - 제공된 데이터만 사용한다.
                - 없는 정보는 추측하지 않는다.
                - 안전점수와 안전시설 분포를 중요하게 설명한다.
                - 거리와 시간 차이는 필요한 경우 함께 언급한다.
                - 1~2문장으로 간결하게 작성한다.
                - 한국어로 작성한다.
                - SHORTEST, BROAD_FIRST 같은 내부 코드명은 사용자에게 보여주지 않는다.

                """
        );

        for (RouteCandidateDto candidate : candidates) {

            SafetyFacilitySummaryDto facility =
                    candidate.getFacilities();

            builder.append("\n[후보 경로]\n");

            builder.append(
                    "경로 유형: "
                            + candidate.getRouteMode()
                            + "\n"
            );

            builder.append(
                    "거리: "
                            + candidate.getDistanceMeter()
                            + "m\n"
            );

            builder.append(
                    "예상 시간: "
                            + candidate.getTimeSecond()
                            + "초\n"
            );

            builder.append(
                    "안전점수: "
                            + candidate.getSafetyScore()
                            + "\n"
            );

            builder.append(
                    "CCTV: "
                            + facility.getCctvCount()
                            + "개\n"
            );

            builder.append(
                    "비상벨: "
                            + facility.getEmergencyBellCount()
                            + "개\n"
            );

            builder.append(
                    "경찰시설: "
                            + facility.getPoliceCount()
                            + "개\n"
            );

            builder.append(
                    "안심지킴이집: "
                            + facility.getSafeHouseCount()
                            + "개\n"
            );

            builder.append(
                    "보안등: "
                            + facility.getSecurityLightCount()
                            + "개\n"
            );

            builder.append(
                    "스마트가로등: "
                            + facility.getSmartLightCount()
                            + "개\n"
            );
        }

        builder.append(
                "\n최종 선택 경로: "
                        + selectedCandidate.getRouteMode()
                        + "\n"
        );

        builder.append(
                "위 데이터를 바탕으로 이 경로를 추천하는 이유만 작성해라."
        );

        return builder.toString();
    }

    private String extractText(
            Map response
    ) {

        if (response == null) {
            return null;
        }

        Object candidatesObject =
                response.get("candidates");

        if (
                !(candidatesObject instanceof List<?> candidates) ||
                candidates.isEmpty()
        ) {
            return null;
        }

        Object firstCandidate =
                candidates.get(0);

        if (
                !(firstCandidate instanceof Map<?, ?> candidateMap)
        ) {
            return null;
        }

        Object contentObject =
                candidateMap.get("content");

        if (
                !(contentObject instanceof Map<?, ?> contentMap)
        ) {
            return null;
        }

        Object partsObject =
                contentMap.get("parts");

        if (
                !(partsObject instanceof List<?> parts) ||
                parts.isEmpty()
        ) {
            return null;
        }

        Object firstPart =
                parts.get(0);

        if (
                !(firstPart instanceof Map<?, ?> partMap)
        ) {
            return null;
        }

        Object text =
                partMap.get("text");

        return text != null
                ? text.toString()
                : null;
    }

    private String createFallbackReason(
            RouteCandidateDto selectedCandidate
    ) {

        SafetyFacilitySummaryDto facility =
                selectedCandidate.getFacilities();

        return String.format(
                "경로 주변 50m 이내에 CCTV %d개와 보안등 %d개 등 "
                        + "안전시설이 확인되어 상대적으로 높은 안전점수를 받은 경로입니다.",
                facility.getCctvCount(),
                facility.getSecurityLightCount()
        );
    }
}
