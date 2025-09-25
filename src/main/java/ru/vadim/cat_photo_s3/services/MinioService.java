package ru.vadim.cat_photo_s3.services;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private static final String PHOTO_BUCKET = "photo";
    private static final String COORDINATION_BUCKET = "coordination";

    @PostConstruct
    private void createBucketIfNotExist() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(PHOTO_BUCKET).build());
            boolean isCoordinationBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(COORDINATION_BUCKET).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(PHOTO_BUCKET).build());
            }
            if (!isCoordinationBucketExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(COORDINATION_BUCKET).build());

            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating bucket " + e.getMessage(), e);
        }
    }

    public String uploadPhoto(String title, MultipartFile file) {
        return upload(title, file, PHOTO_BUCKET);
    }

    public String uploadCoordination(String title, MultipartFile file) {
        return upload(title, file, COORDINATION_BUCKET);
    }

    private String upload(String title, MultipartFile file, String bucket) {
        String path = createPath(title);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file " + e.getMessage(), e);
        }
        return path;
    }

    public InputStream downloadPhoto(String title) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(PHOTO_BUCKET)
                            .object(title)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file " + e.getMessage(), e);
        }
    }

    public InputStream downloadCoordination(String title) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(COORDINATION_BUCKET)
                            .object(title)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error downloading file " + e.getMessage(), e);
        }
    }

    public static String createPath(String title) {
        return LocalDate.now() + "/" + title;
    }
}
