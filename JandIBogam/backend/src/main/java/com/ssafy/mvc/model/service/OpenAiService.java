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

    // 생성자에서 API 키 확인 (디버깅용)
    public OpenAiService() {
        System.out.println("OpenAI API Key loaded: " + (apiKey != null ? "YES" : "NO"));
        if (apiKey != null) {
            System.out.println("API Key starts with: " + apiKey.substring(0, Math.min(10, apiKey.length())));
        }
    }

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
        requestBody.put("model", "gpt-3.5-turbo"); // 추후에 gpt-4o-mini 사용해 볼 예정

        List<Map<String, String>> messages = new ArrayList<>();
        // 시스템 메시지: AI 역할 정의
        messages.add(Map.of(
                "role", "system",
                "content", getSystemPromptForGeneralRecommendation()
        ));
        // 사용자 메시지: 실제 리포트 요약
        messages.add(Map.of(
                "role", "user",
                "content", reportSummary
        ));

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 500);
        requestBody.put("temperature", 0.7);

        return callOpenAiApi(apiUrl, requestBody, headers);
    }

    private String callOpenAiApi(String apiUrl, Map<String, Object> requestBody, HttpHeaders headers) {
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new RuntimeException("OpenAI API 응답이 비정상입니다: HTTP " + response.getStatusCode());
            }

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
            throw new RuntimeException("OpenAI API 호출 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private String getSystemPromptForGeneralRecommendation() {
        return """
                당신은 '다이닝봄' 웹의 전문 AI 영양상담사입니다. 
                
                **역할:**
                - 사용자의 주간 영양 분석 데이터를 바탕으로 맞춤형 식단을 추천 (일주일)
                - 부족하거나 과다한 영양소를 개선할 수 있는 구체적인 방법 제시
                - 한국인의 식문화에 맞는 실용적이고 실현 가능한 조언 제공
                
                **분석 기준:**
                1. 영양소별 상태 (적정/부족/초과) 분석
                2. 사용자의 전반적인 식습관 패턴 파악
                3. 개선이 필요한 우선순위 영양소 식별
                
                **추천 방식:**
                1. 현재 영양 상태에 대한 간단한 분석 (2줄)
                2. 개선이 필요한 주요 영양소와 그 이유 (간단하게)
                3. 구체적인 음식 추천 (한국 음식 위주로 일주일치 아침,점심,저녁 메뉴를 추천하기 보다는 전체적인 일주일 식단 음식 추천)
                4. 실천 가능한 식단 개선 팁
                
                **주의사항:**
                - 너무 복잡하거나 어려운 음식은 피하고 일반적인 한국 가정식 위주로 추천 (고령자 초점)
                - 의학적 진단이나 치료 조언은 하지 말고 일반적인 영양 개선에만 초점
                - 친근하고 격려하는 톤으로 작성
                """;
    }


    // OpenAiService.java에 추가할 메서드

    /**
     * 개인화된 건강 조언 생성 전용 메서드
     */
    public String generateHealthTips(String prompt) {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of(
                "role", "system",
                "content", getSystemPromptForHealthTips()
        ));
        messages.add(Map.of(
                "role", "user",
                "content", prompt
        ));

        requestBody.put("messages", messages);
        requestBody.put("max_tokens", 300); // 조언용이므로 짧게
        requestBody.put("temperature", 0.7);

        return callOpenAiApi(apiUrl, requestBody, headers);
    }

    /**
     * 건강 조언 전용 시스템 프롬프트
     */
    private String getSystemPromptForHealthTips() {
        return """
            당신은 '다이닝봄' 웹의 전문 건강 조언사입니다.
            
            **역할:**
            - 사용자의 영양소 분석 결과를 바탕으로 개인화된 건강 조언 제공
            - 실생활에서 쉽게 실천할 수 있는 구체적이고 실용적인 조언
            - 고령자도 이해하기 쉬운 친근하고 격려하는 톤
            
            **조언 원칙:**
            1. 부족한 영양소는 어떤 음식으로 보충할지 구체적으로 제시
            2. 과다한 영양소는 어떻게 줄일지 실천 방법 제시
            3. 일상생활 습관 개선 방향 제시
            
            **응답 형식:**
            - 정확히 3줄로 구성
            - 각 줄은 하나의 완전한 조언 문장
            - 번호나 특수문자 없이 문장만 작성
            - 각 조언은 30자 이상 50자 이하로 작성
            
            **주의사항:**
            - 의학적 진단이나 치료 조언은 금지
            - 너무 복잡하거나 어려운 방법은 피하기
            - 일반적이고 안전한 건강 상식 위주로 조언
            """;
    }
}
