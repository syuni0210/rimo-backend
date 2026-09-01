package com.ansim.backend.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class KakaoGeoService {

    @Value("${kakao.rest-api-key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public String toRoadAddress(BigDecimal lat, BigDecimal lng) {
        try {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://dapi.kakao.com/v2/local/geo/coord2address.json")
                    .queryParam("x", lng)
                    .queryParam("y", lat)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);

            Map<String, Object> body = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), Map.class
            ).getBody();

            List<Map<String, Object>> documents = (List<Map<String, Object>>) body.get("documents");
            if (documents.isEmpty()) {
                return lat + ", " + lng;
            }

            Map<String, Object> roadAddress = (Map<String, Object>) documents.get(0).get("road_address");
            if (roadAddress != null) {
                return (String) roadAddress.get("address_name");
            }

            Map<String, Object> address = (Map<String, Object>) documents.get(0).get("address");
            return address != null ? (String) address.get("address_name") : lat + ", " + lng;

        } catch (Exception e) {
            e.printStackTrace();
            return lat + ", " + lng;
        }
    }

    public String toKakaoMapUrl(BigDecimal lat, BigDecimal lng) {
        return "https://map.kakao.com/link/map/현재위치," + lat + "," + lng;
    }
}
