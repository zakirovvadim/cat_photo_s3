package ru.vadim.cat_photo_s3.services;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioThumbExist {
    private final MinioClient minio;
    @Value("${minio.photosBucket:photo}")
    private String photosBucket;

    @Value("${minio.thumbsBucket:photo}")
    private String thumbsBucket;

    @Value("${minio.thumbsPrefix:")
    private String thumbsPrefix = "";

    public String thumbKey(String objectName) {
        return thumbsPrefix + objectName;
    }

    public String ensureThumbExist(String objectName) throws Exception {
        if (!thumbsPrefix.isEmpty() && objectName.startsWith(thumbsPrefix)) {
            return objectName;
        }
        String tKey = thumbKey(objectName);
        try {
            minio.statObject(StatObjectArgs.builder().bucket(thumbsBucket).object(tKey).build());
            return tKey;
        } catch (ErrorResponseException e) {
            if (!"NoSuchKey".equals(e.errorResponse().code())) throw e;
        }
        try (InputStream in = minio.getObject(GetObjectArgs.builder()
                .bucket(photosBucket).object(objectName).build());
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Thumbnails.of(in)
                    .size(256, 256)
                    .crop(Positions.CENTER)
                    .outputFormat("jpg")
                    .outputQuality(0.70)
                    .toOutputStream(baos);

            byte[] bytes = baos.toByteArray();
            try (ByteArrayInputStream bin = new ByteArrayInputStream(bytes)) {
                minio.putObject(PutObjectArgs.builder()
                        .bucket(thumbsBucket)
                        .object(tKey)
                        .contentType("image/jpeg")
                        .stream(bin, bytes.length, -1)
                        .build());
            }
        }
        return tKey;
    }
}