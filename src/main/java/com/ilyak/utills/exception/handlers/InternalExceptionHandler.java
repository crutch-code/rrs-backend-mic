package com.ilyak.utills.exception.handlers;

import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

@Singleton
@Primary
@Produces
public class InternalExceptionHandler implements ExceptionHandler<InternalExceptionResponse, MutableHttpResponse<?>> {

    @Override
    public MutableHttpResponse<?> handle(HttpRequest request, InternalExceptionResponse ex) {
        return HttpResponse.serverError(ex.getResponse());
    }
}
