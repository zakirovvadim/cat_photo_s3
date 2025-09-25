package ru.vadim.cat_photo_s3.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vadim.cat_photo_s3.entity.PhotoMetadata;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhotoMetadataRepository extends JpaRepository<PhotoMetadata, Long> {

    PhotoMetadata findByPath(String path);

    List<PhotoMetadata> findByCreationDate(LocalDate localDate);

    boolean existsByPath(String path);
}
