package ru.vadim.cat_photo_s3.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import ru.vadim.cat_photo_s3.entity.Coordination;
import ru.vadim.cat_photo_s3.entity.PhotoMetadata;
import ru.vadim.cat_photo_s3.repository.PhotoMetadataRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoMetadataRepository repository;
    private final MinioService minio;
    private final MinioService minioService;

    @Transactional
    public PhotoMetadata savePhoto(MultipartFile file, MultipartFile coordination) {
        String fileName = file.getOriginalFilename();
        String p = MinioService.createPath(fileName);

        if (checkIfExist(p)) return repository.findByPath(p);

        String path = minio.uploadPhoto(fileName, file);
        String coordinationName = coordination.getOriginalFilename();
        String coordinationPath = minio.uploadCoordination(coordinationName, coordination);

        PhotoMetadata meta = new PhotoMetadata();
        meta.setPath(path);
        meta.setTitle(fileName);
        meta.setExt("jpg");
        meta.setCreationDate(LocalDate.now());
        meta.setCreationDateTime(ZonedDateTime.now());
        meta.setCoordination(Coordination.builder().path(coordinationPath).creationDate(ZonedDateTime.now()).build());
        return repository.save(meta);
    }

    private boolean checkIfExist(String path) {
        return repository.existsByPath(path);
    }

    public StreamingResponseBody getTodayPhotosAsZip() {
        List<PhotoMetadata> byCreationDate = repository.findByCreationDate(LocalDate.now());
        StreamingResponseBody body = outputStream -> {
            try (ZipOutputStream zip = new ZipOutputStream(outputStream)) {
                for (PhotoMetadata meta : byCreationDate) {
                    String path = meta.getPath();
                    String fileName = extractFileName(path);

                    zip.putNextEntry(new ZipEntry(fileName));
                    try (var is = minioService.downloadPhoto(path)) {
                        is.transferTo(zip);
                    }
                    zip.closeEntry();
                }
                zip.finish();
            }
        };
        return body;
    }

    private String extractFileName(String path) {
        if (path == null || path.isBlank()) return "file";
        int i = path.lastIndexOf('/');
        return (i >= 0 && i < path.length() - 1) ? path.substring(i + 1) : path;
    }
}
