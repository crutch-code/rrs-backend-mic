package com.ilyak.repository;

import com.ilyak.entity.jpa.Files;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@Repository
public interface FilesRepository extends CrudRepository<Files, String> {

    Optional<Files> findByFilePath(String filePath);
}
