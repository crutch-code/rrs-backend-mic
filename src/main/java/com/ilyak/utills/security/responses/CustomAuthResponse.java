package com.ilyak.utills.security.responses;

import com.ilyak.entity.User;
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

    public CustomAuthResponse(User credentials) {
        instance = new CustomAuthentication(credentials);
    }

    public CustomAuthResponse(User credentials, Collection<String> roles) {
        instance = new CustomAuthentication(credentials, roles);
    }

    public CustomAuthResponse(User credentials, Collection<String> roles, Map<String, Object> attributes) {
        instance = new CustomAuthentication(credentials, roles, attributes);
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
