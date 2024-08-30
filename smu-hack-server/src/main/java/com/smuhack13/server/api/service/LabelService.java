package com.smuhack13.server.api.service;


import com.smuhack13.server.api.domain.Label;
import com.smuhack13.server.api.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;


    public Label saveResponseData(String country, String userInput, String htmlContent) {
        // 빌더 패턴을 사용하여 ResponseData 객체 생성
        Label label = Label.builder()
                .country(country)
                .userInput(userInput)
                .htmlContent(htmlContent)
                .build();

        return labelRepository.save(label);
    }
}
