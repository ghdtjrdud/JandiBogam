package com.ssafy.mvc.model.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class OpenAiService {

    @Value("${openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 사용자의 주간 리포트 요약(reportSummary)을 OpenAI에 보내고,
     * AI 영양상담사가 작성한 추천 식단 텍스트를 반환합니다.
     *
     * @param reportSummary buildforgpt()로 생성된 문자열
     * @return GPT가 반환한 추천 식단 조언
     * @throws RuntimeException API 호출 실패 시
     */
    public String recommendDiet(String reportSummary) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        // --- 1) 헤더 구성 ---
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // --- 2) 메시지 바디 구성 ---
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo"); // 필요에 따라 "gpt-4" 등으로 변경

        List<Map<String, String>> messages = new ArrayList<>();
        // 시스템 메시지: AI 역할 정의
        messages.add(Map.of(
                "role", "system",
                "content", "" +
                        "당신은 ‘잔디보감’ 프로젝트의 AI 영양상담사입니다.\n" +
                        "사용자가 섭취한 주간 영양소 리포트와 해당 사용자의 질병별 가이드라인을 참고해,\n" +
                        "“이 사용자에게 현재 단계에서 가장 적절한 식단”을 상세히 추천해주세요."
        ));
        // 사용자 메시지: 실제 리포트 요약
        messages.add(Map.of(
                "role", "user",
                "content", reportSummary
        ));

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // --- 3) API 호출 ---
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("OpenAI API 응답이 비정상입니다: HTTP " + response.getStatusCode());
            }

            // --- 4) 응답 파싱 ---
            Map<?, ?> body = response.getBody();
            List<?> choices = (List<?>) body.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new RuntimeException("OpenAI API 응답에 choices가 없습니다.");
            }
            Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
            Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
            Object content = message.get("content");
            if (content == null) {
                throw new RuntimeException("OpenAI API 응답에 message.content가 없습니다.");
            }

            return content.toString().trim();

        } catch (RestClientException e) {
            throw new RuntimeException("OpenAI API 호출 중 오류가 발생했습니다.", e);
        }
    }
}
