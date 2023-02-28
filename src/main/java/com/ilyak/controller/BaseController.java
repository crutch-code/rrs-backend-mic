package com.ilyak.controller;


import com.ilyak.entity.User;
import com.ilyak.entity.responses.DefaultAppResponse;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import com.ilyak.repository.FilesRepository;
import com.ilyak.repository.TransactionalRepository;
import com.ilyak.repository.UserRepository;
import com.ilyak.service.EmailService;
import com.ilyak.service.ErrorService;
import com.ilyak.service.FilesService;
import com.ilyak.service.UserLogoutService;
import com.ilyak.utills.security.CustomAuthentication;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Error;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Inject;

import java.util.Optional;


public class BaseController {

    @Inject
    protected TransactionalRepository transactionalRepository;

    @Inject
    protected FilesRepository filesRepository;

    @Inject
    protected EmailService emailService;

    @Inject
    protected SecurityService securityService;

    @Inject
    protected FilesService filesService;

    @Inject
    protected ErrorService errorService;

    @Inject
    protected UserRepository userRepository;
    @Inject
    protected UserLogoutService logoutService;


    @Value("${micronaut.router.folder.dir-pattern}")
    protected String dirPattern;
    @Value("${micronaut.router.folder.files.post-photos}")
    protected String postPhotos;
    @Value("${micronaut.router.folder.files.avatars}")
    protected String avatars;
    @Value("${micronaut.router.folder.files.documents}")
    protected String documents;
    @Value("${micronaut.router.folder.files.secure-pictures}")
    protected String securePhotos;

    public User getCurrentUser(){
        return ((CustomAuthentication)securityService.getAuthentication().orElse(null)).getCredentials();
    }
    /*public Optional<User> getCurrentUser(){
        return userRepository.findByUserNickName(String.valueOf(securityService.username()));
    }*/

}
