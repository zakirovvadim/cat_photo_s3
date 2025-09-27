package ru.vadim.cat_photo_s3.entity.dto;

public record PhotoInfo(
        String id,
        String filename,
        String url,
        String thumbUrl,
        String takenAt,
        long sizeBytes
) {}
