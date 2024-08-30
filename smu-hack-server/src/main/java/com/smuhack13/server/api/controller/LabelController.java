package com.smuhack13.server.api.controller;


import com.smuhack13.server.api.dto.S3PdfRequest;
import com.smuhack13.server.api.dto.S3PdfResponse;
import com.smuhack13.server.api.service.S3PdfService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LabelController {

    private final S3PdfService s3PdfService;

    @GetMapping("/api/s3Pdf")
    public S3PdfResponse getS3PdfText(@RequestBody S3PdfRequest s3PdfRequest) throws IOException {
        return s3PdfService.getPdfText(s3PdfRequest);
    }
}
