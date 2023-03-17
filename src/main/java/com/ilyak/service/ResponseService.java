package com.ilyak.service;


import com.ilyak.entity.responses.AppResponseWithObject;
import com.ilyak.entity.responses.DefaultAppResponse;
import io.micronaut.http.context.ServerRequestContext;
import jakarta.inject.Singleton;

@Singleton
public class ResponseService {

    public static final int SUCCESS_CODE = 0;

    public static final int INTERNAL_ERROR = 500;

    public static final int UNAUTHORIZED = 401;

    public static final int FORBIDDEN = 403;


    //internals codes

    public static  final int NOT_IMPLEMENTED_YET = 1001;



    public DefaultAppResponse success(){
        return new DefaultAppResponse(
                SUCCESS_CODE,
                "Success completed",
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }

    public DefaultAppResponse success(String message){
        return new DefaultAppResponse(
                SUCCESS_CODE,
                "Success completed: " + message,
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }

    public AppResponseWithObject successWithObject(String message, Object object){
        return new AppResponseWithObject(
                SUCCESS_CODE,
                "Успешно завершено: " + message,
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null),
                object
        );
    }

    public AppResponseWithObject successWithObject(Object object){
        return new AppResponseWithObject(
                SUCCESS_CODE,
                "Успешно завершено",
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null),
                object
        );
    }

    public  DefaultAppResponse error(String message){
        return  new DefaultAppResponse(
                INTERNAL_ERROR,
                "Внутренняя ошибка: " + message,
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }

    public DefaultAppResponse unauthorized(String message){
        return  new DefaultAppResponse(
                UNAUTHORIZED,
                "Пользователь не авторизован: " + message,
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }

    public DefaultAppResponse forbidden(String message){
        return  new DefaultAppResponse(
                FORBIDDEN,
                "Доступ запрещён: " + message,
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }

    public DefaultAppResponse forbidden(){
        return  new DefaultAppResponse(
                FORBIDDEN,
                "Доступ запрещён",
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }
    public DefaultAppResponse toBeImplemented(String message){
        return  new DefaultAppResponse(
                NOT_IMPLEMENTED_YET,
                "This route will be implemented later: " + message,
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }

    public DefaultAppResponse toBeImplemented(){
        return  new DefaultAppResponse(
                NOT_IMPLEMENTED_YET,
                "This solution will be implemented later",
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }
}
