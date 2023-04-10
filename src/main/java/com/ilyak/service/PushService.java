package com.ilyak.service;

import com.fasterxml.jackson.annotation.JsonView;
import com.ilyak.entity.PushMessage;
import com.ilyak.entity.jpa.Post;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.utills.websocket.PingPongMessage;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Singleton
public class PushService {

    @Inject
    PostService postService;
    @Inject
    ResponseService responseService;


    @Inject
    WebSocketBroadcaster broadcaster;

    public Boolean validType(String type){
        return PushType.getInstance(type) != null;
    }

    public Boolean validOpen(String type, String uid, String oid){
        switch (PushType.getInstance(type)){
            case POST -> {
                return postService.valid(uid, oid) ;
            }
            default -> {
                return false;
            }
        }
    }

    public void resolve(PushMessage message, WebSocketSession session){
        switch (message.getType()){
            case POST -> {
                send(postService.resolve(message.getContent()));
            }
            case PONG -> {

            }
            case RATE -> {

            }
            default -> throw new InternalExceptionResponse(
                    "Некорретно передан PushType",
                    responseService.webSocketFailResolve("Некорретно передан PushType")
            );

        }
    }
    @JsonView(JsonViewCollector.PushMessage.BasicView.class)
    public void send(PushMessage message){
        broadcaster.broadcastAsync(
                message,
                session ->
                        session.asMap().get("uid").equals(message.getTarget()) &&
                        session.asMap().get("type").equals(message.getType().title) &&
                        session.asMap().get("oid").equals(message.getOid())
        );
    }

    @JsonView(JsonViewCollector.PushMessage.BasicView.class)
    public void send(String target, PushType type, String oidResource, Object content){
        broadcaster.broadcastAsync(
                new PushMessage(
                        PushType.POST,
                        "unicast",
                        target,
                        oidResource,
                        content,
                        LocalDateTime.now(ZoneId.systemDefault())
                ),
                session ->
                    session.asMap().get("uid") == target &&
                            session.asMap().get("type") == type.title &&
                            session.asMap().get("oid")  == oidResource
                );
    }
    public enum PushType {
        POST("post"),
        PING("ping"),
        PONG("pong"),
        RATE("rating");
        private final String title;

        PushType(String frequency) {
            this.title =frequency;
        }

        public String getTitle() {
            return title;
        }

        public static PushType getInstance(String target){
            return Arrays.stream(PushType.values()).filter(p -> p.title.equals(target)).findFirst().orElse(null);
        }
    }
}
