package com.ilyak.utills.exception.handlers;


import com.ilyak.service.ResponseService;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.security.authentication.AuthenticationException;
import io.micronaut.security.authentication.AuthenticationExceptionHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;


@Singleton
@Produces
@Replaces(AuthenticationExceptionHandler.class)
public class AuthorizationExceptionHandler implements ExceptionHandler<AuthenticationException, MutableHttpResponse<?>> {
    @Inject
    ResponseService responseService;
    @Override
    public MutableHttpResponse<?> handle(HttpRequest request, AuthenticationException exception) {
        return HttpResponse.unauthorized().body(responseService.unauthorized(exception.getMessage()));
    }
}
