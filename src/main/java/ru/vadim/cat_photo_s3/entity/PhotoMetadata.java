package ru.vadim.cat_photo_s3.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.vadim.cat_photo_s3.entity.dto.RegisterPhotoRequestDto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import static ru.vadim.cat_photo_s3.services.MinioService.createPath;

@Table("photo_metadata")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoMetadata {
    @Id
    private Long id;
    private String path;
    private String title;
    private String ext;
    private OffsetDateTime creationDateTime;
    private LocalDate creationDate;
    private Long coordinationId;

    public PhotoMetadata(RegisterPhotoRequestDto dto) {
        this.path = createPath(dto.title());
        this.title = dto.title();
        this.ext = dto.ext();
        this.creationDateTime = dto.creationDateTime().toOffsetDateTime();
        this.creationDate = dto.creationDate();
    }
}
