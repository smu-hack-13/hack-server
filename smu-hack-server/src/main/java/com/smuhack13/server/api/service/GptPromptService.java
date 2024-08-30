package com.smuhack13.server.api.service;

import com.smuhack13.server.api.dto.S3PdfRequest;
import com.smuhack13.server.api.dto.S3PdfResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
public class GptPromptService {

    private final GptService gptService;
    private final S3PdfService s3PdfService;

    public GptPromptService(GptService gptService, S3PdfService s3PdfService) {
        this.gptService = gptService;
        this.s3PdfService = s3PdfService;
    }

    // 사용자 입력과 규정 텍스트를 기반으로 GPT 프롬프트를 생성하여 HTML을 생성하는 메소드
    public Mono<String> generateHtmlWithRegulations(String country, String userInput, String type) {
        try {
            S3PdfRequest s3PdfRequest = S3PdfRequest.builder()
                    .country(country)
                    .type(type)
                    .build();
            S3PdfResponse s3PdfResponse = s3PdfService.getPdfText(s3PdfRequest);

            String prompt = "Using the following regulations for " + country + ":\n\n"
                    + s3PdfResponse.text() + "\n\n"
                    + "Generate an HTML file based on the following user input: "
                    + userInput;

            return gptService.generateHtml(prompt);
        } catch (IOException e) {
            return Mono.error(new RuntimeException("Failed to process PDF file", e));
        }
    }
}

