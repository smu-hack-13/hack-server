package com.smuhack13.server.api.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

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
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + openAiApiKey)
                .bodyValue(Map.of(
                        "model", "gpt-4o",
                        "messages", List.of(
                                Map.of("role", "system", "content", "You are a helpful assistant."),
                                Map.of("role", "user", "content", prompt)
                        ),
                        "max_tokens", 500
                ))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                System.err.println("API 요청 오류: " + errorBody);
                                return Mono.error(new RuntimeException("API 요청 오류: " + errorBody));
                            });
                })
                .bodyToMono(String.class);
    }
}


