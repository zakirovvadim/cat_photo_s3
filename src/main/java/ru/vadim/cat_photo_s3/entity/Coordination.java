package ru.vadim.cat_photo_s3.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.vadim.cat_photo_s3.entity.dto.CoordinationRequestDto;

import java.time.OffsetDateTime;

import static ru.vadim.cat_photo_s3.services.MinioService.createPath;

@Table("coordination")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Coordination {
    @Id
    private Long id;
    private String path;
    private OffsetDateTime creationDate;

    public Coordination(CoordinationRequestDto coordination) {
        this.path = createPath(coordination.path());
        this.creationDate = coordination.creationDate().toOffsetDateTime();
    }
}
