package com.smuhack13.server.api.service;

import com.smuhack13.server.api.dto.S3PdfRequest;
import com.smuhack13.server.api.dto.S3PdfResponse;
import com.smuhack13.server.global.config.AwsConfig;
import java.io.File;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

@Service
public class S3PdfService {

    private final String bucketName;

    private final AwsConfig awsConfig;

    private final S3Client s3Client;

    public S3PdfService(@Value("${aws-property.s3-bucket-name}") final String bucketName,
                        AwsConfig awsConfig,
                        S3Client s3Client) {
        this.bucketName = bucketName;
        this.awsConfig = awsConfig;
        this.s3Client = s3Client;
    }

    public S3PdfResponse getPdfText(S3PdfRequest s3PdfRequest) throws IOException {
        String key = s3PdfRequest.country() + "/" + s3PdfRequest.type() + ".pdf";
        File pdfFile = File.createTempFile("temp", ".pdf");

        s3Client.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key).build(),
                ResponseTransformer.toFile(pdfFile.toPath()));
        return S3PdfResponse.builder().text(extractTextFromPdf(pdfFile)).build();
    }

    private String extractTextFromPdf(File pdfFile) throws IOException {
        PDDocument document = PDDocument.load(pdfFile);
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        return pdfTextStripper.getText(document);
    }

}
