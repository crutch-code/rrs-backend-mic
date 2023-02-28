package com.ilyak.repository;

import com.ilyak.entity.Files;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface FilesRepository extends CrudRepository<Files, String> {


}
