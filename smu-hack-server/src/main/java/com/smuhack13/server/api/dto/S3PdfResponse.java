package com.smuhack13.server.api.dto;

import lombok.Builder;

@Builder
public record S3PdfResponse(String text) {
}
