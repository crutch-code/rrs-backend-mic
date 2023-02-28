package com.ilyak.utills.security;

import com.ilyak.entity.User;
import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.authentication.ServerAuthentication;

import java.util.*;

@Replaces(ServerAuthentication.class)
public class CustomAuthentication extends ServerAuthentication{

    private User credentials;

    private String sessionUUID;





    public CustomAuthentication(User credentials) {
        super(credentials.getUserEmail(), null, null);
        this.credentials = credentials;
        this.sessionUUID = UUID.randomUUID().toString();
    }

    public CustomAuthentication(User credentials, String sessionUUID){
        super(credentials.getUserEmail(), null, null);
        this.credentials = credentials;
        this.sessionUUID = sessionUUID;
    }

    public CustomAuthentication(User credentials, Collection<String> roles, String sessionUUID) {
        super(credentials.getUserEmail(), roles, null);
        this.credentials = credentials;
        this.sessionUUID = sessionUUID;
    }

    public CustomAuthentication(User credentials, Collection<String> roles, Map<String, Object> attributes, String sessionUUID) {
        super(credentials.getUserEmail(), roles, attributes);
        this.credentials = credentials;
        this.sessionUUID = sessionUUID;
    }

    @Override
    public String getName() {
        return credentials.getUserEmail();
    }

    public String getSessionUUID() {
        return sessionUUID;
    }

    public User getCredentials() {
        return credentials;
    }

    public void setCredentials(User credentials) {
        this.credentials = credentials;
    }

}
