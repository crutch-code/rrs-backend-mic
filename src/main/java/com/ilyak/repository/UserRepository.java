package com.ilyak.repository;

import com.ilyak.entity.jpa.User;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;


@Repository
public interface UserRepository extends CrudRepository<User, String> {

     Optional<User> findByUserEmail(String email);

     Optional<User> findByUserPhoneNumber(String email);
     @Query(value = "update User as t set t.userIsConfirm=:confirm where t.oid=:oid")
     void updateUserIsConfirmByOid(String oid, Boolean confirm);
     @Query(value = "update User as t set t.userPhoneNumber=:phone where t.oid=:oid")
     void updateUserPhoneNumberByOid(String phone, String oid);

     @Query(value = "update User as t set t.userPassword=:password where t.oid=:oid")
     void updateUserPasswordByOid(String password, String oid);

}
