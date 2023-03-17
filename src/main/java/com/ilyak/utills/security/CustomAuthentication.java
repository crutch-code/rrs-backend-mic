package com.ilyak.utills.security;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.security.authentication.ServerAuthentication;

import java.util.*;

@Replaces(ServerAuthentication.class)
public class CustomAuthentication extends ServerAuthentication{

    private String uid;

    private String sessionUUID;

    



    public CustomAuthentication(String uid, String login) {
        super(login, null, null);
        this.uid = uid;
        this.sessionUUID = UUID.randomUUID().toString();
    }

    public CustomAuthentication(String uid,String login, String sessionUUID){
        super(login, null, null);
        this.uid = uid;
        this.sessionUUID = sessionUUID;
    }

    public CustomAuthentication(String uid,String login, Collection<String> roles, String sessionUUID) {
        super(login, roles, null);
        this.uid = uid;
        this.sessionUUID = sessionUUID;
    }

    public CustomAuthentication(String uid,String login, Collection<String> roles, Map<String, Object> attributes, String sessionUUID) {
        super(login, roles, attributes);
        this.uid = uid;
        this.sessionUUID = sessionUUID;
    }


    public String getSessionUUID() {
        return sessionUUID;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
