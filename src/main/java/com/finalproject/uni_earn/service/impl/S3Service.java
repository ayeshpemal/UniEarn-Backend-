package com.finalproject.uni_earn.service.impl;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.time.Duration;
import java.net.URL;
import java.io.IOException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Service {

    // Load environment variables from the .env file
    private static final Dotenv dotenv = Dotenv.load();

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    private final String accessKey = dotenv.get("AWS_ACCESS_KEY");

    private final String secretKey = dotenv.get("AWS_SECRET_KEY");

    private S3Client getS3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /**
     * Uploads a file to S3 and returns the file URL.
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = "profile_pictures/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        S3Client s3Client = getS3Client();
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        return fileName;
    }

    /**
     * Generates a pre-signed URL for securely accessing an image.
     */
    public String generatePresignedUrl(String fileName) {
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(60)) // URL valid for 60 minutes
                .getObjectRequest(getObjectRequest)
                .build();

        URL url = presigner.presignGetObject(presignRequest).url();
        return url.toString();
    }
}
