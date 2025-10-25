package ru.vadim.cat_photo_s3.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vadim.cat_photo_s3.entity.PhotoMetadata;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoMetadataRepository extends CrudRepository<PhotoMetadata, Long> {

    List<PhotoMetadata> findByCreationDate(LocalDate localDate);

    @Query("""
        SELECT id, path, title, ext, creation_date_time, creation_date, coordination_id
        FROM photo_metadata
        WHERE path = :path
        LIMIT 1
    """)
    Optional<PhotoMetadata> findOneByPath(@Param("path") String path);

    @Query("""
        SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END
        FROM photo_metadata
        WHERE path = :path
    """)
    int existsIntByPath(@Param("path") String path);
}
