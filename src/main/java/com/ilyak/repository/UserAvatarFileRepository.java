package com.ilyak.repository;

import com.ilyak.entity.jpa.UsersAvatarFile;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface UserAvatarFileRepository extends CrudRepository<UsersAvatarFile, String> {


}
