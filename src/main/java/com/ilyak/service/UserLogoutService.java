package com.ilyak.service;

import com.ilyak.entity.jpa.User;
import io.micronaut.context.annotation.Value;
import io.micronaut.scheduling.TaskScheduler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.time.Duration;
import java.util.HashMap;


@Singleton
public class UserLogoutService {

    @Inject
    TaskScheduler scheduler;


    @Value("${micronaut.security.token.jwt.generator.refresh-token.expiration}")
    private long exp;

    private final HashMap<String, User> logout;


    public UserLogoutService() {

        logout = new HashMap<>();
    }

    public boolean isLogout(String identity){
        return logout.containsKey(identity);
    }


    public User logoutFind(String identity){
        return logout.get(identity);
    }

    public void removeLogout(String identity){
        logout.remove(identity);
    }

    public void putLogout(String identity, User user){
        logout.put(identity, user);
        scheduler.schedule(Duration.ofSeconds(exp), ()-> logout.remove(identity));
    }

}

