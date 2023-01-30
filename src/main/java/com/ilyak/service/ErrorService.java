package com.ilyak.service;


import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.context.annotation.Context;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.context.ServerRequestContext;
import jakarta.inject.Singleton;

@Singleton
public class ErrorService {

    public static final int SUCCESS_CODE = 0;

    public static final int INTERNAL_ERROR = 500;

    public static final int UNAUTHORIZED = 401;



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

    public  DefaultAppResponse error(String message){
        return  new DefaultAppResponse(
                INTERNAL_ERROR,
                "Internal Error: " + message,
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }

    public DefaultAppResponse unauthorized(String message){
        return  new DefaultAppResponse(
                UNAUTHORIZED,
                "User unauthorized: " + message,
                ServerRequestContext.currentRequest().map(m-> m.getPath()).orElse(null)
        );
    }

}
