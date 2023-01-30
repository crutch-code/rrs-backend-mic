package com.ilyak.utills.exception.handlers;


import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.service.ErrorService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Error;
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
    ErrorService errorService;
    @Override
    public MutableHttpResponse<?> handle(HttpRequest request, AuthenticationException exception) {
        return HttpResponse.unauthorized().body(errorService.unauthorized(exception.getMessage()));
    }
}
