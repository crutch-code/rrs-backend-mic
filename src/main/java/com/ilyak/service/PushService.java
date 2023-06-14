package com.ilyak.service;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ilyak.entity.jpa.RentOffer;
import com.ilyak.entity.websocket.PushMessage;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Singleton
public class PushService {
    @Inject
    WebSocketBroadcaster broadcaster;

    public void send(PushMessage message){
        broadcaster.broadcastAsync(
                message,
                session ->
                        session.asMap().get("uid").equals(message.getTarget())
        );
    }

    public enum MessageType{
        PING, PONG, UNICAST, OFFER, RATING
    }


}
