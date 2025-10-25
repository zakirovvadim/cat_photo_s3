package ru.vadim.cat_photo_s3.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;

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
}
