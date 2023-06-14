package com.ilyak.entity.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.MappedSuperclass;


@JsonInclude(value = JsonInclude.Include.NON_EMPTY)
public class DefaultAppResponse {

    protected int internal_code;

    protected String message;

    protected String endpoint;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("is_error_internal")
    public Boolean isError;

    public DefaultAppResponse(){
        message = "Empty response";
        isError= true;
        internal_code = 101;
    }

    public DefaultAppResponse(int internal_code, String message, String endpoint) {
        this.setInternal_code(internal_code);
        this.message = message;
        this.endpoint = endpoint;
    }

    public int getInternal_code() {
        return internal_code;
    }

    public void setInternal_code(int internal_code) {
        this.internal_code = internal_code;
        if(internal_code >99 && internal_code < 200) isError = true;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public Boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
}
