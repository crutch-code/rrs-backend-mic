package com.ilyak.controller;


import com.ilyak.entity.User;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.repository.UserRepository;
import com.ilyak.service.ErrorService;
import com.ilyak.service.UserLogoutService;
import com.ilyak.utills.security.CustomAuthentication;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Error;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Inject;

import java.util.Optional;


public class BaseController {

    @Inject
    protected SecurityService securityService;

    @Inject
    protected ErrorService errorService;

    @Inject
    protected UserRepository userRepository;
    @Inject
    protected UserLogoutService logoutService;

    public User getCurrentUser(){
        return ((CustomAuthentication)securityService.getAuthentication().orElse(null)).getCredentials();
    }
    /*public Optional<User> getCurrentUser(){
        return userRepository.findByUserNickName(String.valueOf(securityService.username()));
    }*/

}
