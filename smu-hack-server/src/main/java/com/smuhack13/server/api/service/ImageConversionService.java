package com.smuhack13.server.api.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ImageConversionService {

    public byte[] convertHtmlToImage(String htmlContent) throws IOException {
        // HTML을 임시 파일로 저장
        File htmlFile = File.createTempFile("temp", ".html");
        Files.write(htmlFile.toPath(), htmlContent.getBytes());

        // wkhtmltoimage 명령어를 사용하여 HTML을 이미지로 변환
        File imageFile = File.createTempFile("temp", ".png");
        ProcessBuilder processBuilder = new ProcessBuilder(
                "wkhtmltoimage", htmlFile.getAbsolutePath(), imageFile.getAbsolutePath());
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("HTML을 이미지로 변환하는 중에 오류 발생", e);
        }

        // 이미지 파일을 byte 배열로 변환하여 반환
        return Files.readAllBytes(imageFile.toPath());
    }
}

