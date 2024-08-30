package com.smuhack13.server.api.service;

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
    public Mono<String> generateHtmlWithRegulations(String country, String userInput) {
        try {
            String regulationsText = s3PdfService.getPdfText(country);
            String prompt = "Using the following regulations for " + country + ":\n\n"
                    + regulationsText + "\n\n"
                    + "Generate an HTML file based on the following user input: "
                    + userInput;

            return gptService.generateHtml(prompt);
        } catch (IOException e) {
            return Mono.error(new RuntimeException("Failed to process PDF file", e));
        }
    }
}

