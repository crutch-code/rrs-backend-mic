package com.ilyak.repository;

import com.ilyak.entity.User;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.reactive.RxJavaCrudRepository;
import io.reactivex.rxjava3.core.Observable;

import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository<User, String> {
     Optional<User> findByUserNickName(String userNickName);

     Optional<User> findByUserEmail(String email);

     Optional<User> findByUserPhoneNumber(String email);

}
