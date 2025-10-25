package ru.vadim.cat_photo_s3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vadim.cat_photo_s3.entity.PhotoMetadata;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhotoMetadataRepository extends CrudRepository<PhotoMetadata, Long> {

    PhotoMetadata findByPath(String path);

    List<PhotoMetadata> findByCreationDate(LocalDate localDate);

    boolean existsByPath(String path);
}
