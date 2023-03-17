package com.ilyak.utills.security.responses;

import com.ilyak.utills.security.CustomAuthentication;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.AuthenticationResponse;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public class CustomAuthResponse implements AuthenticationResponse {

    CustomAuthentication instance;

    public CustomAuthResponse(CustomAuthentication instance) {
        this.instance = instance;
    }

    public CustomAuthResponse(String uid, String login) {
        instance = new CustomAuthentication(uid, login);
    }

    public CustomAuthResponse(String uid, String login, Collection<String> roles, String sessionUUID) {
        instance = new CustomAuthentication(uid, login, roles, sessionUUID);
    }

    public CustomAuthResponse(String uid, String login, Collection<String> roles, Map<String, Object> attributes, String sessionUUID) {
        instance = new CustomAuthentication(uid, login, roles, attributes, sessionUUID);
    }
    @Override
    public boolean isAuthenticated() {
        return getAuthentication().isPresent();
    }

    @Override
    public Optional<Authentication> getAuthentication() {
        return Optional.of(instance);
    }

    @Override
    public Optional<String> getMessage() {
        return AuthenticationResponse.super.getMessage();
    }
}
