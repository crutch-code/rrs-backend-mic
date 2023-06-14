package com.ilyak.repository;

import com.ilyak.entity.jpa.Rating;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface RatingRepository extends CrudRepository<Rating, String> {
}
