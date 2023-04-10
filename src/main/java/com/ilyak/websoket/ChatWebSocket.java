package com.ilyak.websoket;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ilyak.entity.jpa.User;
import com.ilyak.entity.jsonviews.JsonViewCollector;
import com.ilyak.entity.responses.AppResponseWithObject;
import com.ilyak.service.ResponseService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.health.HeartbeatEnabled;
import io.micronaut.http.HttpResponse;
import com.ilyak.controller.BaseController;
import com.ilyak.entity.jpa.Chat;
import com.ilyak.entity.jpa.Message;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.service.ChatService;
import io.micronaut.data.model.Page;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecuredAnnotationRule;
import io.micronaut.validation.Validated;
import io.micronaut.websocket.CloseReason;
import io.micronaut.websocket.WebSocketBroadcaster;
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.*;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;


@ServerWebSocket("/api/chat/{chat_id}")
@Controller("/api/chat")
@Tag(name = "Чат",
        description = "Использутеся для чата между двумя пользователями"
)
@Secured(SecuredAnnotationRule.IS_AUTHENTICATED)
@Validated
@SecurityScheme(name = "BearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "jwt")
public class ChatWebSocket extends BaseController {

    public static final Logger logger = LoggerFactory.getLogger(ChatWebSocket.class);

    @Inject
    WebSocketBroadcaster broadcaster;
    @Inject
    protected ChatService chatService;

    @OnOpen
    public Publisher<DefaultAppResponse> connect(
            WebSocketSession session,
            @PathVariable String chat_id
    ){
        logger.debug("Try connect user: " + getUserId() +" in session: " + session.getId());

        if(!chatService.valid(getCurrentUser().getOid(), chat_id)) {
            session.close(new CloseReason(4003, "Доступ запрещён"));
            return null;
        }
        chatService.addSession(chat_id, getUserId(), session);

        Schedulers
                .newThread()
                .createWorker()
                .schedulePeriodically(heartbeat(session), 0, 6, TimeUnit.SECONDS);

        logger.debug("Success connect");
        return session.send(responseService.success("Соединение установлено"));
    }



    @SneakyThrows
    @OnMessage
    @JsonView(JsonViewCollector.Message.BasicView.class)
    public Publisher<Message> message(
            Message message,
            WebSocketSession session,
            @PathVariable String chat_id
    ) {
        logger.info("Message received. Starting resolving. Sender: " + getUserId());
        Chat current = chatService.getById(chat_id).orElseThrow();

        User target = current.getLeftRecipient().getOid().equals(getUserId())? current.getRightRecipient(): current.getLeftRecipient();
        message.setMessageSender(getCurrentUser());
        message.setMessageChat(current);
        session.sendAsync(responseService.success("Cообщение доставлено"));
        WebSocketSession targetSession = chatService.getSession(chat_id, target.getOid());
        if(targetSession != null && targetSession.isOpen())
            return targetSession.send(chatService.saveMessage(message));
        else {
            chatService.saveMessage(message);
            return Flowable.just(message);
        }
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

    @OnClose
    @JsonView
    public Publisher<DefaultAppResponse> close(
            WebSocketSession session,
            @PathVariable String chat_id
    ){
        chatService.removeSession(chat_id, getUserId());
        return session.send(responseService.webSocketSuccess("Сессия успешно закрыта"));
    }

    public Runnable heartbeat(WebSocketSession session){
        return () -> {
            session.sendPingAsync("hello".getBytes(StandardCharsets.UTF_8));
        };
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получить конкретный чат по OID")
    @Get(uri = "/get/one{?oid}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView
    public HttpResponse<Chat> getChatsByOid(
            @QueryValue Optional<String> oid
    ){
        try{
            return HttpResponse.ok(
                        chatService.getById(oid.orElseThrow()
                    ).orElseThrow(()->new RuntimeException("Идентификтор чата не может быть равен null"))
            );
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Создать чат между двумя пользователями",
            requestBody = @RequestBody(ref = "/chat-create.json")
            
    )
    @Post(uri = "/create", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView
    public HttpResponse<AppResponseWithObject> createChat(
            @Body Chat newChat
    ){
        try{
            return HttpResponse.ok(
                    responseService.successWithObject(
                            "chat created",
                            chatService.createNewChat(newChat)
                    )
            );
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получить сипсок чатов порционно")
    @Get(uri = "/get/list{?page_num,page_size}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.Chat.BasicView.class)
    public HttpResponse<Page<Chat>> getChatsListed(
            @QueryValue @Nullable Integer page_num,
            @QueryValue @Nullable Integer page_size
    ){
        try{
            return HttpResponse.ok(chatService.getChatsForUser(getCurrentUser().getOid(), getPageable(page_num, page_size)));
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получить порцию сообщений из чата")
    @Get(uri = "/message/get/list{?chat_oid,page_num,page_size}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
    @JsonView(JsonViewCollector.Message.BasicView.class)
    public HttpResponse<Page<Message>> getMessages(
            @QueryValue Optional<String> chat_oid,
            @QueryValue @Nullable Integer page_num,
            @QueryValue @Nullable Integer page_size
    ){
        try{
            return HttpResponse.ok(chatService.getMessagesFromChat(
                        chat_oid.orElseThrow(
                                ()-> new RuntimeException("Идентификатор чата не может быть равным null")
                        ), getPageable(page_num, page_size)
                    )
            );
        }catch (Exception ex){
            logger.error(ex.getMessage());
            throw new InternalExceptionResponse(ex.getMessage(), responseService.error(ex.getMessage()));
        }
    }
}
