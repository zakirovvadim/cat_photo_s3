package ru.vadim.cat_photo_s3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.vadim.cat_photo_s3.entity.PhotoMetadata;
import ru.vadim.cat_photo_s3.services.MinioService;
import ru.vadim.cat_photo_s3.services.PhotoService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final MinioService minioService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<PhotoMetadata> upload(@RequestParam("photo") MultipartFile photo,
                                                @RequestParam("textFile") MultipartFile textFile) {
        PhotoMetadata photoMetadata = photoService.savePhoto(photo, textFile);
        return ResponseEntity.ok(photoMetadata);
    }

    @GetMapping("/download/{title}")
    public ResponseEntity<byte[]> download(@PathVariable String title) throws IOException {
        try (InputStream stream = minioService.downloadPhoto(title)) {
            byte[] bytes = stream.readAllBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + title + "\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        }
    }

    @GetMapping("/download/coordination/{title}")
    public ResponseEntity<byte[]> downloadCoordination(@PathVariable String title) throws IOException {
        try (InputStream stream = minioService.downloadCoordination(title)) {
            byte[] bytes = stream.readAllBytes();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + title + "\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(bytes);
        }
    }

    @GetMapping("/download/today")
    public ResponseEntity<StreamingResponseBody> downloadTodayPhotos() throws IOException {
        return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"photos-" + LocalDate.now() + ".zip\"")
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(photoService.getTodayPhotosAsZip());

    }
}