package com.ilyak.entity.responses.exceptions;

import com.ilyak.entity.responses.DefaultAppResponse;

public class InternalExceptionResponse extends RuntimeException{
    private DefaultAppResponse response;

    public InternalExceptionResponse(DefaultAppResponse response) {
        this.response = response;
    }

    public InternalExceptionResponse(String message, DefaultAppResponse response) {
        super(message);
        this.response = response;
    }

    public InternalExceptionResponse(String message, Throwable cause, DefaultAppResponse response) {
        super(message, cause);
        this.response = response;
    }

    public InternalExceptionResponse(Throwable cause, DefaultAppResponse response) {
        super(cause);
        this.response = response;
    }

    public InternalExceptionResponse(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, DefaultAppResponse response) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.response = response;
    }

    public DefaultAppResponse getResponse() {
        return response;
    }
}
