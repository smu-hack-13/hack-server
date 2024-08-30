package com.smuhack13.server.api.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GptService {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String openAiApiKey;

    public GptService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
    }

    // GPT 모델을 호출하여 HTML 콘텐츠를 생성하는 메소드
    public Mono<String> generateHtml(String prompt) {
        return this.webClient.post()
                .uri("/completions")
                .header("Authorization", "Bearer " + openAiApiKey)
                .header("Content-Type", "application/json")
                .bodyValue("{\"model\": \"text-davinci-003\", \"prompt\": \"" + prompt + "\", \"max_tokens\": 500}")
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractHtmlFromGptResponse);
    }

    // GPT 응답에서 HTML을 추출하는 메소드
    private String extractHtmlFromGptResponse(String response) {
        // JSON 파싱 예시 (실제 구조에 따라 수정 필요)
        JSONObject jsonObject = new JSONObject(response);
        return jsonObject.getJSONArray("choices")
                .getJSONObject(0)
                .getString("text");  // 예시로 "text" 필드를 추출
    }
}
