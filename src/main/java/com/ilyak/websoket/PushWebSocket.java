package com.ilyak.websoket;


import com.ilyak.controller.BaseController;
import com.ilyak.entity.PushMessage;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.service.PushService;
import com.ilyak.service.ResponseService;
import com.ilyak.utills.websocket.PingPongMessage;
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

@ServerWebSocket("/api/ws/{type}/{oid}")
@Secured(SecuredAnnotationRule.IS_AUTHENTICATED)
@Validated
public class PushWebSocket  extends BaseController {

    public static final Logger logger = LoggerFactory.getLogger(PushWebSocket.class);

    @Inject
    PushService pushService;

    @Inject
    WebSocketBroadcaster broadcaster;


    @OnOpen
    public Publisher<PushMessage> open(
            WebSocketSession session,
            @PathVariable String oid,
            @PathVariable String type
    ) {
        if (!pushService.validType(type)) {
            session.close(new CloseReason(4500, "Ошибка преобразования типа push: " + type));
            return null;
        }
        String uid = getUserId();

//        if (!pushService.validOpen(type, uid, oid)) {
//            session.close(new CloseReason(4003, "Ошибка доступа"));
//            return null;
//        }

        session.getAttributes().putAll(
                Map.of(
                        "type", type,
                        "oid", oid,
                        "uid", uid,
                        "ping-counter", 0
                )
        );
        Scheduler.Worker pingWorker = Schedulers.newThread().createWorker();
        pingWorker.schedulePeriodically(ping(session, pingWorker), 0, 120, TimeUnit.SECONDS);

        return session.send(
                new PushMessage(
                        PushService.PushType.getInstance(type),
                        uid,
                        uid,
                        oid,
                        responseService.webSocketSuccess("подключение стабильно"),
                        LocalDateTime.now(ZoneId.systemDefault())
                )
        );


    }

    @OnMessage
    public void message(
            PushMessage message,
            WebSocketSession session,
            @PathVariable String oid,
            @PathVariable String type
    ){
        String uid = getUserId();
        logger.info(session.getId() + "keep alive. User:" + uid);
        session.getAttributes().put("ping-counter", 0);
        pushService.send(uid, PushService.PushType.getInstance(type), oid, "message received");
        pushService.resolve(message, session);
        pushService.send(uid, PushService.PushType.getInstance(type), oid, "your message are resolving right now");
    }



    @OnError
    public Publisher<PushMessage> error(
            WebSocketSession session,
            InternalExceptionResponse response,
            @PathVariable String oid,
            @PathVariable String type
    ) {
        logger.error("Error push web socket: " + response.getMessage() + ". Push type: " + type + ". OID: " + oid);
        if (session.isOpen()) {
            logger.debug(session.getId() + "session is opened. UID: " + session.getValue("uid"));
            return session.send(
                    new PushMessage(
                            PushService.PushType.POST,
                            (String) session.getValue("uid"),
                            (String) session.getValue("uid"),
                            (String) session.getValue("oid"),
                            response.getResponse(),
                            LocalDateTime.now()
                    )
            );
        }
        return null;
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
                    new PingPongMessage(
                            PingPongMessage.Type.PING,
                            LocalDateTime.now(ZoneId.systemDefault()
                            )
                    )
            );
        };
    }
}
