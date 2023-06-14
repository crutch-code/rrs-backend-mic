package com.ilyak.controller;


import com.ilyak.entity.jpa.User;
import com.ilyak.repository.*;
import com.ilyak.service.*;
import com.ilyak.utills.security.CustomAuthentication;
import io.micronaut.context.annotation.Value;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.http.MediaType;
import io.micronaut.security.utils.SecurityService;
import jakarta.inject.Inject;

import java.lang.reflect.Field;


public class BaseController {

    @Inject
    protected TransactionalRepository transactionalRepository;
    @Inject
    protected FilesRepository filesRepository;
    @Inject
    protected UserRepository userRepository;

    @Inject
    protected FlatRepository flatRepository;

    @Inject
    protected PostRepository postRepository;

    @Inject
    protected RentOfferRepository rentOfferRepository;

    @Inject
    protected RatingRepository ratingRepository;

    @Inject
    ContractRepository contractRepository;

    @Inject
    protected PostService postService;

    @Inject
    protected EmailService emailService;

    @Inject
    protected TokenGeneratorService generatorService;
    @Inject
    protected SecurityService securityService;

    @Inject
    protected PushService pushService;

    @Inject
    DocumentService documentsService;

    @Inject
    protected FilesService filesService;

    @Inject
    protected ResponseService responseService;


    @Inject
    protected UserLogoutService logoutService;

    @Value("${micronaut.application.default-page-size}")
    protected Integer defaultPageSize;

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

    public Pageable getPageable(Integer pageNum, Integer pageSize) {
        return Pageable.from((pageNum != null) ? pageNum : 0, (pageSize != null) ? pageSize : Integer.MAX_VALUE);
    }

    public Pageable getPageable(Integer pageNum, Integer pageSize, Sort.Order order) {
        return Pageable.from(
                (pageNum != null) ? pageNum : 0,
                (pageSize != null) ? pageSize : Integer.MAX_VALUE,
                Sort.of(order)
        );
    }
    public User getCurrentUser(){
        return userRepository.findById(
                ((CustomAuthentication)securityService.getAuthentication().orElseThrow())
                        .getUid()
        ).orElseThrow();
    }

    public String getUserId(){
        return ((CustomAuthentication)securityService.getAuthentication().orElseThrow(()-> new RuntimeException("Пользователь не найден")))
                .getUid();
    }

    public MediaType mediaTypeFileResolver(String filePath){
        if (filePath == null || filePath.equals("")) throw new RuntimeException("Invalid file name for type resolving");

        switch (filePath.substring(filePath.lastIndexOf(".")+1)){
            case "jpg" -> {
                return MediaType.IMAGE_JPEG_TYPE;
            }
            case "gif" -> {
                return MediaType.IMAGE_GIF_TYPE;
            }
            case "png" -> {
                return MediaType.IMAGE_PNG_TYPE;
            }
            case "pdf" -> {
                return MediaType.APPLICATION_PDF_TYPE;
            }
            default -> {
                return MediaType.APPLICATION_OCTET_STREAM_TYPE;
            }
        }
    }

    public static <T> T updateEntity(T entity, T update) throws IllegalAccessException {
        for (Field field : update.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.get(update) != null) {
                field.set(entity, field.get(update));
            }
        }
        return entity;
    }
}
