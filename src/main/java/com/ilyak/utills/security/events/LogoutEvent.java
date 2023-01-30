package com.ilyak.utills.security.events;

import com.ilyak.entity.User;
import io.micronaut.context.event.ApplicationEvent;

public class LogoutEvent extends ApplicationEvent {
    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    private String TOKEN;

    public LogoutEvent(Object source, String TOKEN) {
        super(source);
        if(TOKEN == null || TOKEN.equals("")) throw new IllegalArgumentException();
        this.TOKEN = TOKEN;
    }

    public LogoutEvent(Object source) {
        super(source);
    }

    public User getCredentials(){
        return (User) source;
    }
}
