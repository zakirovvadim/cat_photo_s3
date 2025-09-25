package ru.vadim.cat_photo_s3.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZonedDateTime;

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
}
