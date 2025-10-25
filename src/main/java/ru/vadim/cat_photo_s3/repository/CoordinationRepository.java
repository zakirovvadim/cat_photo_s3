package ru.vadim.cat_photo_s3.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.vadim.cat_photo_s3.entity.Coordination;

@Repository
public interface CoordinationRepository extends CrudRepository<Coordination, Integer> {
}
