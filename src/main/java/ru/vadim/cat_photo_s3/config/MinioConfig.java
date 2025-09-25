package ru.vadim.cat_photo_s3.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.storage.endpoint}")
    private String endpoint;
    @Value("${minio.storage.login}")
    private String login;
    @Value("${minio.storage.password}")
    private String password;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(login, password)
                .build();
    }
}
