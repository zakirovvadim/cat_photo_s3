package ru.vadim.cat_photo_s3.controller;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.vadim.cat_photo_s3.entity.PhotoMetadata;
import ru.vadim.cat_photo_s3.entity.dto.PhotoInfo;
import ru.vadim.cat_photo_s3.services.MinioService;
import ru.vadim.cat_photo_s3.services.MinioThumbExist;
import ru.vadim.cat_photo_s3.services.PhotoService;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final MinioService minioService;
    private final MinioThumbExist thumbService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public ResponseEntity<PhotoMetadata> upload(@RequestParam("photo") MultipartFile photo,
                                                @RequestParam("textFile") MultipartFile textFile) {
        PhotoMetadata meta = photoService.savePhoto(photo, textFile);
        return ResponseEntity.ok(meta);
    }

    @GetMapping("/download/{title}")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable String title) {
        String key = java.net.URLDecoder.decode(title, java.nio.charset.StandardCharsets.UTF_8);
        StreamingResponseBody body = out -> {
            try (InputStream in = minioService.streamPhoto(key)) {
                in.transferTo(out);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\"")
                .contentType(MediaType.IMAGE_JPEG)
                .body(body);
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
    public ResponseEntity<StreamingResponseBody> downloadTodayPhotos() {
        return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"photos-" + LocalDate.now() + ".zip\"")
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .body(photoService.getTodayPhotosAsZip());

    }

    @GetMapping("/photos")
    public List<PhotoInfo> listPhotos() {
        log.info("listing photos");
        List<Item> items = minioService.listPhotos();
        List<PhotoInfo> result = new ArrayList<>(items.size());
        for (Item item : items) {
            String name = item.objectName();
            long size = item.size();

            String iso = parseIsoFromName(name);
            result.add(new PhotoInfo(name,
                    name,
                    "/api/download?id=" + encodeForPath(name),
                    "/api/thumb?id=" + encodeForPath(name),
                    iso,
                    size));
        }
        result.sort(Comparator.comparing((PhotoInfo p) -> Optional.ofNullable(p.takenAt()).orElse(""))
                .reversed()
                .thenComparing(PhotoInfo::filename));
        return result;
    }

    @GetMapping("/thumb")
    public ResponseEntity<Void> thumb(@RequestParam("id") String id) {
        log.info("thumb id: " + id);
        String objectName = java.net.URLDecoder.decode(id, java.nio.charset.StandardCharsets.UTF_8);
        try {
            String tKey = thumbService.ensureThumbExist(objectName);
            String url  = minioService.presignedThumbUrl(tKey, 10 * 60);
            return ResponseEntity.status(302)
                    .location(java.net.URI.create(url))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                    .build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadByParam(@RequestParam("id") String id) {
        log.info("try to download");
        String key = java.net.URLDecoder.decode(id, java.nio.charset.StandardCharsets.UTF_8);

        StreamingResponseBody body = out -> {
            try (InputStream in = minioService.streamPhoto(key)) {
                in.transferTo(out);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        MediaType ct = MediaType.IMAGE_JPEG;
        try {
            var stat = minioService.statPhoto(key);
            String s = stat.contentType();
            if (s != null && !s.isBlank()) ct = MediaType.parseMediaType(s);
        } catch (Exception ignore) {}

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\"")
                .contentType(ct)
                .body(body);
    }

    private static final Pattern DATE_RE = Pattern.compile(
            "(?<y>20\\d{2}|19\\d{2})[-_]?(?<m>\\d{2})[-_]?(?<d>\\d{2})(?:[-_](?<H>\\d{2})[-_]?(?<M>\\d{2})(?:[-_]?(?<S>\\d{2}))?)?"
    );

    private static String parseIsoFromName(String name) {
        Matcher m = DATE_RE.matcher(name);
        if (!m.find()) return null;
        int y = Integer.parseInt(m.group("y"));
        int mo = Integer.parseInt(m.group("m"));
        int d = Integer.parseInt(m.group("d"));
        int H = m.group("H") != null ? Integer.parseInt(m.group("H")) : 12;
        int M = m.group("M") != null ? Integer.parseInt(m.group("M")) : 0;
        int S = m.group("S") != null ? Integer.parseInt(m.group("S")) : 0;
        return OffsetDateTime.of(y, mo, d, H, M, S, 0, ZoneOffset.UTC).toString();
    }

    // Если в имени есть пробелы/русские символы — аккуратно кодируем для URL-пути
    private static String encodeForPath(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }
}