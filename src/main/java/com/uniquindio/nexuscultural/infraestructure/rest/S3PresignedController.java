package com.uniquindio.nexuscultural.infraestructure.rest;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/storage")
public class S3PresignedController {

    private final ResourceLoader resourceLoader;

    @Value("${aws.s3.credentials-path}")
    private String credentialsPath;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.region}")
    private String region;

    private String accessKey;
    private String secretKey;

    public S3PresignedController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    public void initCredentials() {
        try {
            Resource resource = resourceLoader.getResource(credentialsPath);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                // Línea 1: Encabezados ("Access key ID,Secret access key")
                reader.readLine();

                // Línea 2: Datos ("AKIA...,wJalr...")
                String dataLine = reader.readLine();
                if (dataLine != null && !dataLine.isBlank()) {
                    String[] parts = dataLine.split(",");
                    this.accessKey = parts[0].trim();
                    this.secretKey = parts[1].trim();
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo cargar el archivo de credenciales de AWS desde: " + credentialsPath, e);
        }
    }

    @GetMapping("/presigned-url")
    public Map<String, String> generarPresignedUrl(
            @RequestParam("extension") String extension,
            @RequestParam("contentType") String contentType) {

        String key = "obras/" + UUID.randomUUID() + "." + extension;
        Region awsRegion = Region.of(region);

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );

        try (S3Presigner presigner = S3Presigner.builder()
                .region(awsRegion)
                .credentialsProvider(credentialsProvider)
                .build()) {

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);

            String uploadUrl = presignedRequest.url().toString();
            String publicUrl = "https://" + bucket + ".s3." + awsRegion.id() + ".amazonaws.com/" + key;

            return Map.of(
                    "uploadUrl", uploadUrl,
                    "publicUrl", publicUrl
            );
        }
    }
}