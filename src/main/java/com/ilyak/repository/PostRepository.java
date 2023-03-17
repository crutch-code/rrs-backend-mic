package com.ilyak.repository;

import com.ilyak.entity.jpa.Post;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface PostRepository extends CrudRepository<Post, String> {
}
