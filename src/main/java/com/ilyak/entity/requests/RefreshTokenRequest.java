package com.ilyak.entity.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.sun.istack.NotNull;
import io.micronaut.core.annotation.Introspected;

import javax.validation.constraints.NotBlank;

@Introspected
public class RefreshTokenRequest {

    @NotBlank
    @NotNull
    @JsonAlias({"token, refresh_token"})
    protected String token;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
