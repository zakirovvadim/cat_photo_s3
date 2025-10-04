package ru.vadim.cat_photo_s3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vadim.cat_photo_s3.entity.dto.RegisterPhotoRequestDto;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static ru.vadim.cat_photo_s3.services.MinioService.createPath;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    private String title;
    private String ext;
    private ZonedDateTime creationDateTime;
    private LocalDate creationDate;
    @OneToOne(cascade = CascadeType.ALL)
    private Coordination coordination;

    public PhotoMetadata(RegisterPhotoRequestDto dto) {
        this.path = createPath(dto.title());
        this.title = dto.title();
        this.ext = dto.ext();
        this.creationDateTime = dto.creationDateTime();
        this.creationDate = dto.creationDate();
        this.coordination = new Coordination(dto.coordination());
    }
}
