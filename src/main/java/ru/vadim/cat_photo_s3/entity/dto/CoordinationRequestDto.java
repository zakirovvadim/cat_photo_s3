package ru.vadim.cat_photo_s3.entity.dto;

import java.time.ZonedDateTime;

public record CoordinationRequestDto(String path,
                                     ZonedDateTime creationDate) {

}
