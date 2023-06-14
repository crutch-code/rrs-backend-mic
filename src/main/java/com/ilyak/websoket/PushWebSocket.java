package com.ilyak.websoket;


import com.ilyak.controller.BaseController;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.websocket.PushMessage;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.service.PushService;
import com.ilyak.entity.websocket.PingPongMessage;
import com.ilyak.service.ResponseService;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.validation.Validated;
import io.micronaut.websocket.CloseReason;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnError;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@ServerWebSocket("/api/ws/{oid}")
@Secured(SecuredAnnotationRule.IS_AUTHENTICATED)
@Validated
public class PushWebSocket  extends BaseController {

    public static final Logger logger = LoggerFactory.getLogger(PushWebSocket.class);

    @OnOpen
    public Publisher<PushMessage> connect(
            WebSocketSession session,
            @PathVariable String oid
    ){
        logger.info("Try connect user: " + getUserId() +" in session: " + session.getId());
        if (!getUserId().equals(oid)){
            session.close(CloseReason.UNSUPPORTED_DATA);
            return null;
        }

        Scheduler.Worker pingWorker = Schedulers.newThread().createWorker();
        pingWorker.schedulePeriodically(ping(session, pingWorker), 0, 120, TimeUnit.SECONDS);

        session.getAttributes().putAll(
                Map.of(
                        "uid", oid,
                        "ping-counter", 0
                )
        );

        return session.send(
                new PushMessage(
                        PushService.MessageType.UNICAST,
                        "unicast",
                        oid,
                        LocalDateTime.now(ZoneId.systemDefault()),
                        "Соединение успешно установлено")
        );
    }

    @OnMessage
    public void message(
            PushMessage message,
            WebSocketSession session,
            @PathVariable String oid
    ){
        session.getAttributes().put("ping-counter", 0);
        session.sendAsync(new PushMessage(
                PushService.MessageType.UNICAST,
                "unicast",
                oid,
                LocalDateTime.now(ZoneId.systemDefault()),
                "Сообщение получено типа: "  + message.getType()));
    }



    @OnError
    public Publisher<DefaultAppResponse> error(
            WebSocketSession session,
            InternalExceptionResponse response
    ) {
        if (session.isOpen()) {
            if (response.getResponse().getInternal_code() == ResponseService.FORBIDDEN) {
                return  session.send(response.getResponse());
            }
            return session.send(response.getResponse());
        }
        return Flowable.just(response.getResponse());
    }

    Runnable ping(WebSocketSession session, Scheduler.Worker worker){
        return ()->{
            Integer count = (Integer) session.asMap().get("ping-counter");
            if(count == 2){
                session.close(CloseReason.GOING_AWAY);
            }
            if(!session.isOpen()) {
                worker.dispose();
                return;
            }
            session.getAttributes().put("ping-counter", ++count);
            session.sendAsync(
                    new PushMessage(
                            PushService.MessageType.PING,
                            "unicast",
                            session.getAttributes().asMap().get("uid").toString(),
                            LocalDateTime.now(ZoneId.systemDefault()),
                            "ping-message"
                    )
            );
        };
    }
}
