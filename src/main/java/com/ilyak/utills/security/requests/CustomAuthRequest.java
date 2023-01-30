package com.ilyak.utills.security.requests;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.security.authentication.AuthenticationRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Introspected
public class CustomAuthRequest implements AuthenticationRequest {

    @NotBlank
    @NotNull
    @JsonProperty("name")
    @JsonAlias("login")
    protected String login;

    @NotBlank
    @NotNull
    protected String password;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    @Override
    public Object getIdentity() {
        return getLogin();
    }

    @JsonIgnore
    @Override
    public Object getSecret() {
        return getPassword();
    }
}
