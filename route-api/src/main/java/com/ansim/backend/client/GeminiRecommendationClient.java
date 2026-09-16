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

        String cacheKey = buildCacheKey(selectedCandidate);

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

        String reason = callGemini(selectedCandidate, candidates);

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

    private String callGemini(
            RouteCandidateDto selectedCandidate,
            List<RouteCandidateDto> candidates
    ) {

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

        try {
            Map response = restClient.post()
                    .uri("/v1beta/models/{model}:generateContent?key={apiKey}", model, apiKey)
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(Map.class);

            List candidatesList = (List) response.get("candidates");
            Map firstCandidate = (Map) candidatesList.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            String text = (String) firstPart.get("text");

            return text.trim();

        } catch (Exception e) {
            System.err.println("Gemini API 호출 실패: " + e.getMessage());
            return createFallbackReason(selectedCandidate);
        }
    }

    private String buildPrompt(
            RouteCandidateDto selectedCandidate,
            List<RouteCandidateDto> candidates
    ) {

        StringBuilder sb = new StringBuilder();
        sb.append("다음은 안전 경로 추천 후보들입니다.\n\n");

        for (RouteCandidateDto c : candidates) {
            sb.append(String.format(
                    "- %s: 거리 %dm, 시간 %d초, 안전점수 %.1f\n",
                    c.getRouteMode(),
                    c.getDistanceMeter(),
                    c.getTimeSecond(),
                    c.getSafetyScore()
            ));
        }

        sb.append(String.format(
                "\n최종 선택된 경로는 '%s'이며 안전점수는 %.1f입니다.\n",
                selectedCandidate.getRouteMode(),
                selectedCandidate.getSafetyScore()
        ));

        sb.append("이 경로가 왜 선택되었는지 자연스러운 한글 문장 1~2개로 설명해주세요.");

        return sb.toString();
    }

    private String createFallbackReason(RouteCandidateDto selectedCandidate) {
        SafetyFacilitySummaryDto f = selectedCandidate.getFacilities();

        return String.format(
                "경로 주변 50m 이내에 CCTV %d개와 보안등 %d개 등 " +
                        "안전시설이 확인되어 상대적으로 높은 안전점수를 받은 경로입니다.",
                f.getCctvCount(),
                f.getSecurityLightCount()
        );
    }
}
