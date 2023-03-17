package com.ilyak.service;

import com.ilyak.entity.jpa.Chat;
import com.ilyak.entity.jpa.Message;
import com.ilyak.entity.jpa.User;
import com.ilyak.repository.ChatRepository;
import com.ilyak.repository.MessageRepository;
import com.ilyak.repository.TransactionalRepository;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.websocket.WebSocketSession;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.mail.Session;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Singleton
public class ChatService {


    @Inject
    TransactionalRepository transactionalRepository;
    @Inject
    ChatRepository chatRepository;

    @Inject
    MessageRepository messageRepository;

    private final Map<String, Map<String, WebSocketSession>> sessionContainer = new HashMap<>();

    public Boolean valid(String uid, String chatId){
        return chatRepository.valid(uid, chatId);
    }

    //todo: добавить проверку на возможность создания чата
    public Chat createNewChat(User creator, User receiver){
        return chatRepository.save(
                new Chat(
                        transactionalRepository.genOid().orElseThrow(),
                        LocalDateTime.now(ZoneId.systemDefault()),
                        LocalDateTime.now(ZoneId.systemDefault()),
                        creator,
                        receiver,
                        null
                )
        );
    }

    public Chat createNewChat(Chat instance){
        instance.setOid(transactionalRepository.genOid().orElseThrow());
        instance.setCreationDate(LocalDateTime.now(ZoneId.systemDefault()));
        instance.setCreationDate(LocalDateTime.now(ZoneId.systemDefault()));
        return chatRepository.save(
                instance
        );
    }

    //todo: implement
    public Chat deleteChatHistory(Boolean forAll){
        return null;
    }

    public void saveMessage(Message message){
        message.setOid(transactionalRepository.genOid().orElseThrow(()->new RuntimeException("Ошибка генерации идентификатора")));
        messageRepository.save(message);
        chatRepository.updateActivity(
                message.getMessageChat().getOid(),
                message.getOid(),
                LocalDateTime.now(ZoneId.systemDefault())
        );
    }

    public Optional<Chat> getById(String oid){
        return chatRepository.findById(oid);
    }

    public Page<Message> getMessagesFromChat(String chatOid, Pageable pageable){
        return messageRepository.getMessageByMessageChat(chatOid,pageable);
    }

    public Page<Chat> getChatsForUser(String uid, Pageable pageable){
        return chatRepository.findByLeftRecipientOrRightRecipient(uid, pageable);
    }

    public void addSession(String uid, WebSocketSession session){
        if (!sessionContainer.containsKey(uid))
            sessionContainer.put(uid, CollectionUtils.mapOf(session.getId(), session));
        sessionContainer.get(uid).put(session.getId(), session);
    }

    public void removeSession(String uid, WebSocketSession session){
        if (sessionContainer.containsKey(uid))
            sessionContainer.get(uid).remove(session.getId());
    }

    public Optional<WebSocketSession> getSession(String uid, WebSocketSession session){
        return Optional.of(sessionContainer.get(uid).get(session.getId()));
    }
}
