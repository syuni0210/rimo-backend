package com.ansim.backend.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SolapiSmsService {

    @Value("${solapi.api-key}")
    private String apiKey;

    @Value("${solapi.api-secret}")
    private String apiSecret;

    @Value("${solapi.sender}")
    private String sender;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendSms(String receiverPhone, String subject, String message) {
    try {
        String url = "https://api.solapi.com/messages/v4/send";

        String date = ZonedDateTime.now()
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String salt = generateSalt();
        String signature = makeSignature(date, salt);

        String authHeader = String.format(
                "HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s",
                apiKey, date, salt, signature
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authHeader);

        Map<String, Object> messagePayload = new HashMap<>();
        messagePayload.put("to", receiverPhone.replaceAll("-", ""));
        messagePayload.put("from", sender);
        messagePayload.put("type", "LMS");
        messagePayload.put("subject", subject);
        messagePayload.put("text", message);

        Map<String, Object> body = new HashMap<>();
        body.put("message", messagePayload);

        var response = restTemplate.postForEntity(
                url, new HttpEntity<>(body, headers), String.class
        );

        return response.getStatusCode().is2xxSuccessful();

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    private String generateSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private String makeSignature(String date, String salt) throws Exception {
        String data = date + salt;

        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder sb = new StringBuilder();
        for (byte b : rawHmac) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
