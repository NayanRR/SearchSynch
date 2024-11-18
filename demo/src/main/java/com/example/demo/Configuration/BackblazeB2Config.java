//package com.example.demo.Configuration;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.beans.factory.annotation.Value;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.S3Configuration;
//import software.amazon.awssdk.services.s3.S3Configuration.Builder;
//
//import java.net.URI;
//
//@Configuration
//public class BackblazeB2Config {
//
//
//    @Value("${backblaze.b2.applicationKeyId}")
//    private String applicationKeyId;
//
//
//    @Value("${backblaze.b2.applicationKey}")
//    private String applicationKey;
//
//    @Value("${backblaze.b2.endpoint}")
//    private String endpoint;
//
//    @Bean
//    public S3Client s3Client() {
//
//        return S3Client.builder()
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create(applicationKeyId, applicationKey)
//                ))
//                .endpointOverride(URI.create(endpoint))
//                .serviceConfiguration(S3Configuration.builder()
//                        .pathStyleAccessEnabled(true) // Required for S3-compatible storage
//                        .build())
//                .region(Region.US_WEST_1) // Specify any region; it will default to your endpoint
//                .build();
//    }
//}

