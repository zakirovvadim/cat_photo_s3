package ru.vadim.cat_photo_s3.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import ru.vadim.cat_photo_s3.entity.dto.CoordinationRequestDto;

import java.time.ZonedDateTime;

import static ru.vadim.cat_photo_s3.services.MinioService.createPath;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Coordination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    private ZonedDateTime creationDate;

    public Coordination(CoordinationRequestDto coordination) {
        this.path = createPath(coordination.path());
        this.creationDate = coordination.creationDate();
    }
}
