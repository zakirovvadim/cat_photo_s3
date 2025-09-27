package ru.vadim.cat_photo_s3.services;

import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.photosBucket:photo}")
    private String photosBucket;

    @Value("${minio.thumbsBucket:photo}")
    private String thumbsBucket;

    @Value("${minio.coordinationBucket:coordination}")
    private String coordination;

    @Value("${minio.thumbsPrefix:thumbs/}")
    private String thumbsPrefix;

    @PostConstruct
    private void createBucketIfNotExist() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(photosBucket).build());
            boolean isCoordinationBucketExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(coordination).build());
            boolean isThumbsBucket = minioClient.bucketExists(BucketExistsArgs.builder().bucket(thumbsBucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(photosBucket).build());
            }
            if (!isCoordinationBucketExist) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(coordination).build());
            }
            if (!isThumbsBucket) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(thumbsBucket).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating bucket " + e.getMessage(), e);
        }
    }

    public String uploadPhoto(String title, MultipartFile file) {
        return upload(title, file, photosBucket);
    }

    public String uploadCoordination(String title, MultipartFile file) {
        return upload(title, file, coordination);
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
                            .bucket(photosBucket)
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
                            .bucket(coordination)
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

    public List<Item> listPhotos() {
        List<Item> out = new ArrayList<>();
        Iterable<Result<Item>> it = minioClient.listObjects(ListObjectsArgs.builder().bucket(photosBucket).recursive(true).build());
        for (Result<Item> r : it) {
            try {
                Item item = r.get();
                out.add(item);
            } catch (Exception ignore) {}
        }
        return out;
    }

    public StatObjectResponse statPhoto(String objectName) throws Exception {
        return minioClient.statObject(
                StatObjectArgs.builder().bucket(photosBucket).object(objectName).build());
    }
    public InputStream streamPhoto(String objectName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs.builder().bucket(photosBucket).object(objectName).build());
    }

    public InputStream streamFrom(String bucket, String object) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(object).build());
    }

    public String presignedThumbUrl(String thumbObjectKey, int expirySeconds) throws Exception {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(thumbsBucket)
                .object(thumbObjectKey)
                .expiry(expirySeconds)
                .build());
    }
}
