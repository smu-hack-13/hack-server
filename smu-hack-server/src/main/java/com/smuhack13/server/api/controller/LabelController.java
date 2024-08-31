package com.smuhack13.server.api.controller;


import com.smuhack13.server.api.dto.GenerateLabelRequest;
import com.smuhack13.server.api.dto.S3PdfRequest;
import com.smuhack13.server.api.dto.S3PdfResponse;
import com.smuhack13.server.api.service.GptPromptService;
import com.smuhack13.server.api.service.ImageConversionService;
import com.smuhack13.server.api.service.LabelService;
import com.smuhack13.server.api.service.S3PdfService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LabelController {

    private final S3PdfService s3PdfService;
    private final GptPromptService gptPromptService;
    private final LabelService labelService;


    @GetMapping("/s3Pdf")
    public S3PdfResponse getS3PdfText(@RequestBody S3PdfRequest s3PdfRequest) throws IOException {
        return s3PdfService.getPdfText(s3PdfRequest);
    }

    @PostMapping("/generate-label")
    public ResponseEntity<Resource> generateLabel(@RequestBody GenerateLabelRequest request) throws IOException {
        Mono<String> htmlContentMono = gptPromptService.generateHtmlWithRegulations(
                request.getCountry(), request.getUserInput(), request.getType()
        );

        String htmlContent = htmlContentMono.block();

        // HTML 콘텐츠를 데이터베이스에 저장
        labelService.saveResponseData(request.getCountry(), request.getUserInput(), htmlContent);

        // HTML 콘텐츠만 추출
        String extractedHtmlContent = extractHtmlFromGptResponse(htmlContent);

        // HTML을 임시 파일로 저장
        Path htmlFilePath = Files.write(Paths.get("output.html"), extractedHtmlContent.getBytes());

        // 파일을 반환
        Resource resource = new FileSystemResource(htmlFilePath.toFile());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"output.html\"")
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    // GPT 응답에서 <!DOCTYPE html>로 시작하고 </html>로 끝나는 HTML만 추출하는 메소드
    private String extractHtmlFromGptResponse(String response) {
        // GPT 응답을 JSON으로 파싱
        JSONObject jsonObject = new JSONObject(response);
        String fullText = jsonObject.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content");

        // <!DOCTYPE html> 로 시작하고 </html>로 끝나는 부분만 추출
        int htmlStart = fullText.indexOf("<!DOCTYPE html");
        int htmlEnd = fullText.lastIndexOf("</html>") + 7;

        if (htmlStart != -1 && htmlEnd != -1 && htmlEnd > htmlStart) {
            return fullText.substring(htmlStart, htmlEnd);
        } else {
            return "No valid HTML content found in the response.";
        }
    }
}



