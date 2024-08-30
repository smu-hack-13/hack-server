package com.smuhack13.server.api.service;

import com.smuhack13.server.api.dto.S3PdfRequest;
import com.smuhack13.server.api.dto.S3PdfResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

            String pdfText = s3PdfResponse.text();
            List<String> splitText = splitTextIntoChunks(pdfText, 100000);
            List<Mono<String>> gptResponses = new ArrayList<>();

            for (String chunk : splitText) {
                String prompt = "Using the following regulations for " + country + ":\n\n"
                        + chunk + "\n\n"
                        + "Generate an HTML file based on the following user input: "
                        + userInput;
                gptResponses.add(gptService.generateHtml(prompt));
            }

            // Object[] 대신 String[]로 변환하여 join 메서드 사용
            return Mono.zip(gptResponses, results -> String.join("\n", Arrays.copyOf(results, results.length, String[].class)))
                    .doOnError(error -> System.err.println("오류 발생: " + error.getMessage()));

        } catch (IOException e) {
            return Mono.error(new RuntimeException("Failed to process PDF file", e));
        }
    }

    private List<String> splitTextIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        for (int start = 0; start < text.length(); start += chunkSize) {
            chunks.add(text.substring(start, Math.min(text.length(), start + chunkSize)));
        }
        return chunks;
    }
}

