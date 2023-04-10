package com.ilyak.repository;


import com.ilyak.entity.jpa.Message;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface MessageRepository extends CrudRepository<Message, String> {

    @Query(value = "from Message as t where t.messageChat.oid=:chatOid order by t.messageSendTime",
        countQuery = "select count (t) from Message as t where t.messageChat.oid=:chatOid"
    )
    Page<Message> getMessageByMessageChat(String chatOid, Pageable pageable);
}
