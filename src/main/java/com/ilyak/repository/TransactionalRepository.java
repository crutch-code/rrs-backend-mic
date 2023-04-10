package com.ilyak.repository;


import io.micronaut.data.annotation.Repository;
import org.hibernate.type.StringType;

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

    @Transactional
    public void deleteFile(String oid, String path, String targetManyToManyServiceTable){
        manager.createNativeQuery("call delete_file(cast(:oid as text), cast(:path as text), cast(:target as text))" )
                .setParameter("oid", oid)
                .setParameter("path", path)
                .setParameter("target", targetManyToManyServiceTable)
                .executeUpdate();
    }
}
