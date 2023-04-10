package com.ilyak.repository;


import com.ilyak.entity.jpa.Chat;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ChatRepository extends CrudRepository<Chat, String> {

    @Query(value = "update Chat as t " +
            "set t.lastActivity=:lastActivity  " +
            "where t.oid=:oid"
    )
    Optional<Chat> updateActivity(String oid, LocalDateTime lastActivity);

    @Query(
            value = "from Chat as t where t.leftRecipient.oid=:uid or t.rightRecipient.oid=:uid",
            countQuery = "select count (t) from Chat as t where t.leftRecipient.oid=:uid or t.rightRecipient.oid=:uid"
    )
    Page<Chat> findByLeftRecipientOrRightRecipient(String uid, Pageable pageable);

    @Query(value = "select exists(select from chat as t where t.oid=:chatOid " +
            "and (t.chat_left_user_recipient=:uid or t.chat_right_user_recipient=:uid))", nativeQuery = true)
    Boolean valid(String uid, String chatOid);
}
