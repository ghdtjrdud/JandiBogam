package com.ssafy.mvc.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class OpenAiService {

    @Value("${openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String recommendDiet(String reportSummary) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // OpenAI 메시지 포맷
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo"); // 또는 "gpt-4"
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "당신은 ‘잔디보감’ 프로젝트의 AI 영양상담사입니다.\n" +
                "사용자가 섭취한 하루치(또는 주간) 영양소 리포트와, 해당 사용자의 진단받은 질병별 영양소 가이드라인을 바탕으로\n" +
                "“이 사용자에게 지금 단계에서 가장 필요한 식단”을 추천해주세요.."));
        messages.add(Map.of("role", "user", "content", reportSummary));
        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 요청 보내고 응답 받기
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

        Map result = response.getBody();
        List choices = (List) result.get("choices");
        Map choice = (Map) choices.get(0);
        Map message = (Map) choice.get("message");
        return (String) message.get("content");
    }
}
