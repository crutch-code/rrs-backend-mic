package com.ilyak.utills.security.events;

import com.ilyak.utills.security.CustomAuthentication;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.event.LoginSuccessfulEvent;

public class CustomLoginSuccessfulEvent extends LoginSuccessfulEvent {
    /**
     * Event triggered when a successful login takes place.
     *
     * @param source the {@link Authentication} of the person logging in.
     * @throws IllegalArgumentException if source is null.
     */
    private String authToken;

    public CustomLoginSuccessfulEvent(Object source, String authToken) {
        super(source);
        if(authToken == null || authToken.equals("")) throw new IllegalArgumentException();
        this.authToken = authToken;
    }

    public CustomLoginSuccessfulEvent(Object source) {
        super(source);
    }

    public CustomAuthentication getCustomAuthentication(){
        return (CustomAuthentication) source;
    }

    public String getAuthToken() {
        return authToken;
    }
}
