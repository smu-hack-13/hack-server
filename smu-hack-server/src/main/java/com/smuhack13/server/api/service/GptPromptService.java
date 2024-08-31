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

            // 처음 1000자만 사용
            String pdfText = s3PdfResponse.text();
            String truncatedText = pdfText.substring(0, Math.min(1000, pdfText.length()));

            String prompt = "Using the following regulations for " + country + ":\n\n"
                    + truncatedText + "\n\n"
                    + "Generate an HTML file based on the following user input: "
                    + userInput;


//            String request_information = String.format("입력된 영양정보는 영양성분명과 함량, 1일 영양성분 기준치에 대한 비율을 각 영양성분마다 나타낸것입니다.\n" +
//                            "아래의 요청사항들을 모두 철저히 준수해서 %s의 %s영양성분 표기 규정에 맞게 작성된 영양성분표를 웹에서 바로 확인할 수 있도록 HTML 코드를 작성해 주세요.\n" +
//                            "요청사항:\n" +
//                            "1. 영양성분 나열 순서 확인: %s의 %s영양성분 표기 규정에 따라 입력된 영양성분명을 순서에 맞게 올바르게 배치합니다.\n" +
//                            "2. 추가 문구 확인: %s의 %s영양성분 표기 규정에 따라 추가적으로 필요한 문구가 있는지 확인합니다.\n" +
//                            "3. 필요 없는 항목 확인: %s의 %s영양성분 표기 규정에 따라 영양성분 함량과 1일 영양성분 기준치 비율 중에서 규정상 필요 없는 항목이 있는지 확인합니다.\n" +
//                            "4. 요소의 굵기 및 크기 확인: %s의 %s영양성분 표기 규정에 따라 각 요소가 영양성분인지, 함량인지, 1일 영양성분 기준치에 대한 비율인지 확인하고 종류별로 규정에 따른 적절한 글씨 굵기와 크기를 확인합니다.\n" +
//                            "5. 라인 개수 확인: %s의 %s영양성분 표기 규정에 따라 영양성분표에 포함되어야 하는 라인의 수를 확인합니다.\n" +
//                            "6. 라인 굵기 확인: %s의 %s영양성분 표기 규정에 따라 영양성분표에 포함되는 라인의 종류를 확인하고 그에 맞는 적절한 굵기를 확인합니다.\n" +
//                            "7. 요소 간 거리 확인: %s의 %s영양성분 표기 규정에 따라 각 요소가 영양성분인지, 함량인지, 1일 영양성분 기준치에 대한 비율인지 확인하고 그에 따른 요소 간 올바른 거리를 확인합니다.",
//                    country, type, country, type, country, type, country, type, country, type, country, type, country, type, country, type);
//
//            String prompt = userInput + "\n" + request_information;




            return gptService.generateHtml(prompt);

        } catch (IOException e) {
            return Mono.error(new RuntimeException("Failed to process PDF file", e));
        }
    }
}


