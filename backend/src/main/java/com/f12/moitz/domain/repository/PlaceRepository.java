package com.f12.moitz.domain.repository;

import com.f12.moitz.domain.Place;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlaceRepository extends MongoRepository<Place, String> {

    boolean existsByName(final String name);

    Optional<Place> findByName(final String name);

}
