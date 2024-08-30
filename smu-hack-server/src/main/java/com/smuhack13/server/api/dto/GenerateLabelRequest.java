package com.smuhack13.server.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GenerateLabelRequest {

    private String country;
    private String userInput;
    private String type;
}
