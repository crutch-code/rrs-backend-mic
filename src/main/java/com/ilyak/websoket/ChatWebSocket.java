package com.ilyak.websoket;

import com.ilyak.entity.responses.AppResponseWithObject;
import com.ilyak.service.ResponseService;
import io.micronaut.core.annotation.Nullable;
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
import io.micronaut.websocket.WebSocketSession;
import io.micronaut.websocket.annotation.OnError;
import io.micronaut.websocket.annotation.OnMessage;
import io.micronaut.websocket.annotation.OnOpen;
import io.micronaut.websocket.annotation.ServerWebSocket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


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
    protected ChatService chatService;

    //todo: Проверка чата
    @OnOpen
    public Publisher<DefaultAppResponse> connect(
            WebSocketSession session,
            @PathVariable String chat_id
    ){
        logger.debug("Try connect user: " + getCurrentUser().getOid() +" in session: " + session.getId());

        if(!chatService.valid(getCurrentUser().getOid(), chat_id))
            throw new InternalExceptionResponse(responseService.forbidden());
        chatService.addSession(getCurrentUser().getOid(), session);
        return session.send(responseService.success("Соединение установлено"));
    }

    @OnMessage
    public Publisher<String> message(
            Message message,
            WebSocketSession session,
            @PathVariable String chat_id
    ){
        logger.info(message.getMessage());
        Chat current = chatService.getById(chat_id).orElseThrow(
                ()-> new InternalExceptionResponse(
                        "Ошибка получения чата по oid: " + chat_id,
                        responseService.error("Ошибка получения чата по oid: " + chat_id)
                )
        );
        return session.send(message + "received");
    }

    @OnError
    public Publisher<HttpResponse<DefaultAppResponse>> error(
            WebSocketSession session,
            InternalExceptionResponse response
    ){
        if(response.getResponse().getInternal_code() == ResponseService.FORBIDDEN){
            return session.send(HttpResponse.unauthorized().body(response.getResponse()));
        }
        return session.send(HttpResponse.serverError(response.getResponse()));
    }

    @ExecuteOn(TaskExecutors.IO)
    @Operation(summary = "Получить конкретный чат по OID")
    @Get(uri = "/get/one{?oid}", produces = MediaType.APPLICATION_JSON_STREAM)
    @SecurityRequirement(name = "BearerAuth")
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
