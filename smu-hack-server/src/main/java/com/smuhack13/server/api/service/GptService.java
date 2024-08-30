package com.smuhack13.server.api.service;

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
        // GPT 응답에서 HTML 내용을 추출
        return this.webClient.post()
                .uri("/completions")
                .header("Authorization", "Bearer " + openAiApiKey)
                .bodyValue("{\"model\": \"text-davinci-003\", \"prompt\": \"" + prompt + "\", \"max_tokens\": 500}")
                .retrieve()
                .bodyToMono(String.class)
                .map(this::extractHtmlFromGptResponse);
    }

    // GPT 응답에서 HTML을 추출하는 메소드
    private String extractHtmlFromGptResponse(String response) {
        // 실제로는 JSON 파싱 로직이 필요할 수 있습니다.
        return response;  // 기본 예제에서는 응답을 그대로 반환
    }
}
