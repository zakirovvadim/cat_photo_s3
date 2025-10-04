package ru.vadim.cat_photo_s3.entity.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;


public record RegisterPhotoRequestDto
        (String title,
         String ext,
         ZonedDateTime creationDateTime,
         LocalDate creationDate,
         CoordinationRequestDto coordination) {
}
