package com.ilyak.service;

import com.ilyak.entity.User;
import com.ilyak.utills.security.events.CustomLoginSuccessfulEvent;
import com.ilyak.utills.security.events.LogoutEvent;
import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.TaskScheduler;
import io.micronaut.scheduling.annotation.Async;
import io.micronaut.security.token.event.AccessTokenGeneratedEvent;
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

//    @EventListener
//    @Async
//    public void loginEventListener(final CustomLoginSuccessfulEvent e) {
//
//
//        for(var entry : logout.entrySet()){
//            if(entry.getValue().equals(e.getCustomAuthentication().getCredentials()));
//        }
//
//    }
//
//    @EventListener
//    @Async
//    public void tokenValidEventListener(final AccessTokenGeneratedEvent e){
//        System.out.println(e.toString());
//    }
//
//    @EventListener
//    @Async
//    public void logoutEventListener(final LogoutEvent e){
//        logout.put(e.getCredentials().getUserNickName(), e.getCredentials());
//    }
}

