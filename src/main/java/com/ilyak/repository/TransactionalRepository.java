package com.ilyak.repository;


import io.micronaut.data.annotation.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public class TransactionalRepository {

    @PersistenceContext
    EntityManager manager;


    @Transactional
    public Optional<String> genOid(){
        return Optional.of(String.valueOf(manager.createNativeQuery("select gen_id()").getSingleResult()));
    }

}
