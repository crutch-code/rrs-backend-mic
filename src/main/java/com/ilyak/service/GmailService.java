package com.ilyak.service;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.ilyak.entity.responses.exceptions.InternalExceptionResponse;
import io.micronaut.context.annotation.Value;
import io.micronaut.runtime.event.ApplicationStartupEvent;
import io.micronaut.runtime.event.annotation.EventListener;
import io.micronaut.scheduling.annotation.Async;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;

@Singleton
public class GmailService {


    public static final Logger gmailLog = LoggerFactory.getLogger(GmailService.class);


    @Value("${micronaut.router.folder.dir-pattern}")
    private String dirPattern;

    @Inject
    private ResponseService responseService;

    @Value("${google.mail.credentials.client-secret}")
    private String secret;
    @Value("${google.mail.credentials.client-id}")
    private String client_id;

    private LocalServerReceiver receiver;

    private Credential credential;

    private Gmail instance;


    public Gmail instance(){
        if (instance == null) throw new InternalExceptionResponse(
                "Gmail service isn't instantiate",
                responseService.error("Gmail service isn't instantiate")
        );
        return instance;
    }

    @SneakyThrows
    @EventListener
    @Async
    public void instantiate(ApplicationStartupEvent e){
        receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        credential = new AuthorizationCodeInstalledApp(
                new GoogleAuthorizationCodeFlow.Builder(
                        new NetHttpTransport(),
                        JacksonFactory.getDefaultInstance(),
                        client_id,
                        secret,
                        Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM)
                ).setAccessType("offline")
                        .setDataStoreFactory(
                        new FileDataStoreFactory(new File(dirPattern + "google-credentials"))
                ).build(),
                receiver
        ).authorize("remote.rent.system.mail");

        instance = new Gmail.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                credential
        ).setApplicationName("Remote Rent System").build();
    }

}
