package com.ilyak.entity.requests.security;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.sun.istack.NotNull;
import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;

@Introspected
public class TokenRequest {

    @NotBlank
    @NotNull
    @JsonAlias({"token, refresh_token"})
    protected String token;

    public TokenRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
