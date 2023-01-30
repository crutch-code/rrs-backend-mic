package com.ilyak.utills.security;

import com.ilyak.entity.User;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.ServerAuthentication;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Replaces(ServerAuthentication.class)
public class CustomAuthentication extends ServerAuthentication{

    private User credentials;


    public CustomAuthentication(User credentials) {
        super(credentials.getUserNickName(), null, null);
        this.credentials = credentials;
    }

    public CustomAuthentication(User credentials, Collection<String> roles) {
        super(credentials.getUserNickName(), roles, null);
        this.credentials = credentials;

    }

    public CustomAuthentication(User credentials, Collection<String> roles, Map<String, Object> attributes) {
        super(credentials.getUserNickName(), roles, attributes);
        this.credentials = credentials;
    }

    @Override
    public String getName() {
        return credentials.getUserNickName();
    }

    public User getCredentials() {
        return credentials;
    }

    public void setCredentials(User credentials) {
        this.credentials = credentials;
    }

}
