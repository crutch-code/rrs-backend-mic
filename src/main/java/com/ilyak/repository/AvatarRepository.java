package com.ilyak.repository;


import io.micronaut.data.annotation.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Repository
public class AvatarRepository {

    @PersistenceContext
    EntityManager manager;

    @Transactional
    public void addAvatar(){

    }

    @Transactional
    public void addDefaultAvatar(String uid){
        manager.createNativeQuery("insert into users_avatar_file as t values (default, t.user_oid=:uid, default)")
                .setParameter("uid", uid)
                .executeUpdate();
    }
}
